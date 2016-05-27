package org.apodhrad.p2diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.util.UnpackUtils;

public class Diff {

	private static boolean separator;

	private static ArrayList<URL> originalUrls = new ArrayList<URL>();
	private static ArrayList<URL> revisedUrls = new ArrayList<URL>();

	private static String filter = "(.*)";
	private static File extract1 = new File("diffs/beta");
	private static File extract2 = new File("diffs/final");
	private static File target = new File("diffs/index.html");

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				System.out.println(IOUtils.toString(Diff.class.getResourceAsStream("/help.txt")));
			} else {
				if (args[0].equals("-f")) {
					Properties settings = new Properties();
					settings.load(new FileInputStream(args[1]));

					propertiesConfig(settings);
				} else {
					consoleConfig(args);
				}

				JDownloadManager jdm = new JDownloadManager();

				File unpackZip1 = new File(extract1.getPath() + "/zip1-ext");
				File unpackZip2 = new File(extract2.getPath() + "/zip2-ext");

				for (URL url : originalUrls) {
					UnpackUtils.unpack(jdm.download(url.getFile(), extract1), unpackZip1);
				}

				for (URL url : revisedUrls) {
					UnpackUtils.unpack(jdm.download(url.getFile(), extract2), unpackZip2);
				}

				System.out.println(unpackZip2.getAbsolutePath());

				DiffManager dm = new DiffManager(unpackZip1, unpackZip2, filter, target);
				FileUtils.writeStringToFile(target, dm.generateHTML());

				InputStream css = Diff.class.getResourceAsStream("/css/style.css");
				InputStream js = Diff.class.getResourceAsStream("/js/script.js");

				FileUtils.writeStringToFile(new File(target.getParent() + "/css/style.css"), readInputStream(css));
				FileUtils.writeStringToFile(new File(target.getParent() + "/js/script.js"), readInputStream(js));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readInputStream(InputStream is) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer);
		return writer.toString();
	}

	private static void propertiesConfig(Properties settings) throws MalformedURLException {
		if (settings.containsKey("filter"))
			filter = settings.getProperty("filter");
		if (settings.containsKey("target"))
			target = new File(settings.getProperty("target"));
		if (settings.containsKey("extract1"))
			extract1 = new File(settings.getProperty("extract1"));
		if (settings.containsKey("extract2"))
			extract2 = new File(settings.getProperty("extract2"));
		if (settings.containsKey("original"))
			originalUrls = getURLArray(settings.getProperty("original"));
		if (settings.containsKey("revised"))
			revisedUrls = getURLArray(settings.getProperty("revised"));
	}

	private static ArrayList<URL> getURLArray(String origin) throws MalformedURLException {
		ArrayList<URL> result = new ArrayList<URL>();

		String[] half = origin.split(",");
		for (String item : half) {
			result.add(new URL(item));
		}

		return result;
	}

	private static void consoleConfig(String[] args) throws Exception {
		for (String argument : args) {
			if (argument.equals("."))
				separator = true;
			else {
				if (!argument.startsWith("--")) {
					if (!separator) {
						originalUrls.add(new URL(argument));
					} else {
						revisedUrls.add(new URL(argument));
					}
				} else {
					String[] parts = argument.split("=");
					parts[0] = parts[0].replace("--", "");

					if (parts.length > 1) {
						switch (parts[0]) {
						case "filter":
							filter = parts[1];
							break;
						case "extract1":
							extract1 = new File(parts[1]);
							break;
						case "extract2":
							extract2 = new File(parts[1]);
							break;
						case "target":
							target = new File(parts[1]);
							break;
						default:
							throw new Exception("This configuration does not exists (" + parts[0] + ")");
						}
					} else {
						throw new Exception("This configuration does not exists (" + parts[0] + ")");
					}
				}
			}
		}
	}
}
