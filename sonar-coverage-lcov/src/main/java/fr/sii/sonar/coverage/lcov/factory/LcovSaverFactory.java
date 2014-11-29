package fr.sii.sonar.coverage.lcov.factory;

import fr.sii.sonar.coverage.core.factory.CoverageSaverFactory;
import fr.sii.sonar.coverage.lcov.domain.LcovReport;
import fr.sii.sonar.coverage.lcov.saver.LcovSaver;
import fr.sii.sonar.report.core.PluginContext;
import fr.sii.sonar.report.core.exception.CreateException;
import fr.sii.sonar.report.core.save.Saver;

/**
 * Factory that creates a LcovSaver instance with provided sonar context
 * 
 * @author aurelien
 *
 */
public class LcovSaverFactory implements CoverageSaverFactory<LcovReport> {

	public Saver<LcovReport> create(PluginContext pluginContext) throws CreateException {
		return new LcovSaver(pluginContext);
	}

}
