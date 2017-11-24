/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.kyj.fx.voeditor.visual.component.pmd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.pmd.GargoylePMDConfiguration;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public final class PMDGargoyleThreadProcessor extends AbstractPMDProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(PMDGargoyleThreadProcessor.class);

	public PMDGargoyleThreadProcessor(GargoylePMDConfiguration configuration) {
		super(configuration);
	}

	public void processFiles(RuleSetFactory ruleSetFactory, List<DataSource> files, RuleContext ctx, List<Renderer> renderers) {

		// single threaded execution

		RuleSets rs = createRuleSets(ruleSetFactory);
		SourceCodeProcessor processor = new SourceCodeProcessor(configuration);

		for (DataSource dataSource : files) {
			String niceFileName = filenameFrom(dataSource);

			Report report = PMD.setupReport(rs, ctx, niceFileName);
			// overtake the listener
			//bug fix 2016-10-05 by kyj. 결과가 중복되서 출력됨.
			//			report.addSynchronizedListeners(ctx.getReport().getSynchronizedListeners());
			//	        ctx.setReport(report);
			//	        ctx.setSourceCodeFilename(niceFileName);

			//			if (LOG.isLoggable(Level.FINE)) {
			//				LOG.fine("Processing " + ctx.getSourceCodeFilename());
			//			}
			rs.start(ctx);

			for (Renderer r : renderers) {
				r.startFileAnalysis(dataSource);
			}

			try {
				InputStream stream = new BufferedInputStream(dataSource.getInputStream());
				//				ctx.setLanguageVersion(null);
				processor.processSourceCode(stream, rs, ctx);
			} catch (PMDException pmde) {

				//				LOGGER.error(ValueUtil.toString(pmde));
				//				if (LOG.isLoggable(Level.FINE)) {
				//					LOG.log(Level.FINE, "Error while processing file: " + niceFileName, pmde.getCause());
				//				}

				report.addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
			} catch (IOException ioe) {
				//				LOGGER.error(ValueUtil.toString(ioe));
				// unexpected exception: log and stop executor service
				addError(report, "Unable to read source file", ioe, niceFileName);
			} catch (RuntimeException re) {
				//				LOGGER.error(ValueUtil.toString(re));
				// unexpected exception: log and stop executor service
				addError(report, "RuntimeException while processing file", re, niceFileName);
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			rs.end(ctx);

			super.renderReports(renderers, ctx.getReport());
		}
	}

	private void addError(Report report, String msg, Exception ex, String fileName) {
		LOGGER.error(ValueUtil.toString(ex));
		report.addError(new Report.ProcessingError(ex.getMessage(), fileName));
	}
}
