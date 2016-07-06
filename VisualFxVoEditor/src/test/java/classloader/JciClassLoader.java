/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2015. 10. 23.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package classloader;

/**
 * 
 * 작동은 잘 되나 JDK1.8버젼에서 작동불가...
 * 
 * @author KYJ
 *
 */
public class JciClassLoader {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(JciClassLoader.class);
//
//	// 패키지는 /로 표현한다. 라이브러리 자체가 이렇게 구현되있다. -ㅅ-;
//	String[] sources = { "/com/kyj/fx/voeditor/visual/util/DialogUtil.java" };
//
//	// 파일패쓰는 \\
//	String parentSourcePath = "H:\\javafxWorksapce\\VisualFxVoEditor\\src\\main\\java";
//	String parentTargetPath = "C:\\Users\\KYJ\\JCIExample";
//
//	File sourceDir = new File(parentSourcePath);
//	File targetDir = new File(parentTargetPath);
//
//	@Test
//	public void compileTest() {
//		JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");
//		targetDir.mkdirs();
//		
//		FileResourceReader pReader = new FileResourceReader(sourceDir);
//		FileResourceStore pStore = new FileResourceStore(targetDir);
//		ClassLoader classLoader = getClass().getClassLoader();
//		JavaCompilerSettings pSettings = new JavaCompilerSettings();
//		pSettings.setSourceEncoding("UTF-8");
//		pSettings.setSourceVersion("1.7");
//		CompilationResult result = compiler.compile(sources, pReader, pStore, classLoader, pSettings);
//		LOGGER.debug(result.getErrors().length + " errors");
//		LOGGER.debug(result.getWarnings().length + " warnings");
//		LOGGER.debug("#####################################");
//		LOGGER.debug(sourceDir.getAbsolutePath());
//		CompilationProblem[] errors = result.getErrors();
//
//		for (CompilationProblem c : errors) {
//			LOGGER.debug(c.toString());
//		}
//	}
}
