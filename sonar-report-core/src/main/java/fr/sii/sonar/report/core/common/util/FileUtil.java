package fr.sii.sonar.report.core.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.InputFile.Type;
import org.sonar.api.resources.File;

import fr.sii.sonar.report.core.common.PluginContext;
import fr.sii.sonar.report.core.common.exception.SaveException;

/**
 * Utility that helps to find a file either into Sonar system abstraction or
 * into real file system.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileUtil {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Search for real file on the file system using the provided absolute or
	 * relative path. The relative path provided in a report may represent the
	 * path either relative to the project, relative to a source directory or
	 * relative to a test directory. This helper searches into the provided list
	 * of parents. The first file that exists on the system file is the returned
	 * one. If the path is absolute, then it is returned if it really exists.
	 * 
	 * @param path
	 *            The absolute or relative path to the file
	 * @param possibleParents
	 *            The list of directories that may contain the file
	 * @return the system file if found, null otherwise
	 */
	public static java.io.File getSystemFile(String path, List<java.io.File> possibleParents) {
		for (java.io.File parent : possibleParents) {
			java.io.File file = new java.io.File(parent, path);
			if (file.exists()) {
				return file;
			}
		}
		java.io.File absoluteFile = new java.io.File(path);
		return absoluteFile.exists() ? absoluteFile : null;
	}

	/**
	 * Helper function that checks if the sonar file exists. If the file doesn't
	 * exist, and if the configuration value for missing file is true, then an
	 * exception is thrown. If the file doesn't exist and if the configuration
	 * value for missing file is false, then an log is written to indicate that
	 * the file is missing.
	 * 
	 * @param pluginContext
	 *            The plugin context used to get the configuration value
	 * @param sonarFile
	 *            The sonar file (may be null)
	 * @param path
	 *            The path to the real file on the system (may point to a file
	 *            that doesn't exist)
	 * @param message
	 *            An additional message to add in the log
	 * @throws SaveException
	 *             when the file is missing and the plugin is configured to fail
	 * @return true if the file exists, false if the file doesn't exist and the
	 *         plugin is configured to not fail
	 */
	public static boolean checkMissing(PluginContext pluginContext, File sonarFile, String path, String message) throws SaveException {
		if (sonarFile == null) {
			if (pluginContext.getSettings().getBoolean(pluginContext.getConstants().getMissingFileFailKey())) {
				throw new SaveException("The file " + path + " doesn't exist");
			} else {
				LOG.warn("The file " + path + " doesn't exist. " + message);
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Helper function that checks if the sonar file exists. If the file doesn't
	 * exist, and if the configuration value for missing file is true, then an
	 * exception is thrown. If the file doesn't exist and if the configuration
	 * value for missing file is false, then an log is written to indicate that
	 * the file is missing.
	 * 
	 * @param pluginContext
	 *            The plugin context used to get the configuration value
	 * @param sonarFile
	 *            The sonar file (may be null)
	 * @param path
	 *            The path to the real file on the system (may point to a file
	 *            that doesn't exist)
	 * @param message
	 *            An additional message to add in the log
	 * @throws SaveException
	 *             when the file is missing and the plugin is configured to fail
	 * @return true if the file exists, false if the file doesn't exist and the
	 *         plugin is configured to not fail
	 */
	public static boolean checkMissing(PluginContext pluginContext, InputFile sonarFile, String path, String message) throws SaveException {
		if (sonarFile == null) {
			if (pluginContext.getSettings().getBoolean(pluginContext.getConstants().getMissingFileFailKey())) {
				throw new SaveException("The file " + path + " doesn't exist");
			} else {
				LOG.warn("The file " + path + " doesn't exist. " + message);
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Get the relative path from the provided path. The path will be compared
	 * to provided parent directories. If the file is relative to one of these
	 * parents, the relative path will be returned. If the path is already
	 * relative, then the path is directly returned.
	 * 
	 * @param path
	 *            the path to make relative if not
	 * @param possibleParents
	 *            the parent directories that may contain the file
	 * @return the relative path
	 * @throws IOException
	 *             if the path couldn't be read on file system
	 */
	public static String getRelativePath(String path, java.io.File... possibleParents) throws IOException {
		return getRelativePath(path, Arrays.asList(possibleParents));
	}

	/**
	 * Get the relative path from the provided path. The path will be compared
	 * to provided parent directories. If the file is relative to one of these
	 * parents, the relative path will be returned. If the path is already
	 * relative, then the path is directly returned.
	 * 
	 * @param path
	 *            the path to make relative if not
	 * @param possibleParents
	 *            the parent directories that may contain the file
	 * @return the relative path
	 * @throws IOException
	 *             if the path couldn't be read on file system
	 */
	public static String getRelativePath(String path, List<java.io.File> possibleParents) throws IOException {
		java.io.File file = new java.io.File(path);
		if (file.isAbsolute()) {
			for (java.io.File parent : possibleParents) {
				if (file.getCanonicalPath().contains(parent.getCanonicalPath())) {
					return file.getCanonicalPath().replaceFirst(".*" + parent.getCanonicalPath() + "/?(.+$)", "$1");
				}
			}
			return null;
		} else {
			return path;
		}
	}

	/**
	 * Search a file for the provided path on the Sonar file system. The path
	 * must not be null.
	 * 
	 * <p>
	 * You can also provide a filter to get only source file ({@link Type#MAIN})
	 * or test file ({@link Type#TEST}).
	 * 
	 * @param fileSystem
	 *            the Sonar file system
	 * @param path
	 *            the path of the file to load
	 * @param types
	 *            the type filters
	 * @return the found Sonar file or null if not found
	 * @throws IllegalArgumentException
	 *             if path is null
	 */
	public static InputFile getInputFile(FileSystem fileSystem, String path, Type... types) {
		if (path == null) {
			throw new IllegalArgumentException("The path of the file must not be null");
		}
		// search the file accros the file system
		FilePredicate searchPredicate = searchFilePredicate(fileSystem, path);
		// filter according to provided type(s)
		FilePredicate typePredicate = filterTypePredicate(fileSystem, types);
		// get the file matching both path and types
		return fileSystem.inputFile(fileSystem.predicates().and(typePredicate, searchPredicate));
	}

	private static FilePredicate searchFilePredicate(FileSystem fileSystem, String path) {
		FilePredicates predicatesFactory = fileSystem.predicates();
		Collection<FilePredicate> searchPredicates = new ArrayList<FilePredicate>();
		// try to find the file directly using the provided path (absolute or
		// relative)
		searchPredicates.add(predicatesFactory.hasPath(path));
		// if not found, maybe the path starts with '/'
		// in this case, Sonar thinks it's an absolute path => manually try
		// relative
		if (path.startsWith("/")) {
			searchPredicates.add(predicatesFactory.hasRelativePath(path.substring(1)));
		}
		// if not found, try to search it everywhere
		searchPredicates.add(predicatesFactory.matchesPathPattern("**" + path));
		return predicatesFactory.or(searchPredicates);
	}

	private static FilePredicate filterTypePredicate(FileSystem fileSystem, Type... types) {
		FilePredicates predicatesFactory = fileSystem.predicates();
		Collection<FilePredicate> typePredicates = new ArrayList<FilePredicate>(types.length);
		for (Type type : types) {
			typePredicates.add(predicatesFactory.hasType(type));
		}
		return typePredicates.isEmpty() ? predicatesFactory.all() : predicatesFactory.or(typePredicates);
	}
}
