package simple;
/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/

/**
 * 
 *  JCI 테스트 종료.
 *  
 * 작동은 잘 되나 JDK1.8버젼에서 작동불가...
 * @author KYJ
 *
 */
public class JCITest {
//
//	private String sourceDir = "H:\\TestFolder\\source\\src";
//	private String targetDir = "H:\\TestFolder\\target\\classes";
//
//	@Test
//	public void test() throws Exception {
//
//		FieldMeta fieldMeta = new FieldMeta((Modifier.PRIVATE), "sample", String.class);
//		FieldMeta fieldMeta2 = new FieldMeta((Modifier.PRIVATE), "sample2", String.class);
//		FieldMeta fieldMeta3 = new FieldMeta((Modifier.PRIVATE), "check", boolean.class);
//		FieldMeta fieldMeta4 = new FieldMeta((Modifier.PRIVATE), "count", int.class);
//		FieldMeta fieldMeta5 = new FieldMeta((Modifier.PRIVATE), "name", StringProperty.class, SimpleStringProperty.class);
//
//		/*
//		 * , FxVo.class, new Class<?>[] { IExtractClass .class, IExtractField
//		 * .class }
//		 */
//		ClassMeta classMeta = new ClassMeta("", "Simple");
//		VoEditor voEditor = new VoEditor(classMeta, fieldMeta, fieldMeta2, fieldMeta3, fieldMeta4, fieldMeta5);
//		voEditor.build();
//
//		File text = voEditor.toFile("C:\\TestFolder\\source");
//		System.out.println(text);
//
//		Assert.assertEquals(true, text.exists());
//
//		text.delete();
//	}
//
//	@Test
//	public void tutorialTest() {
//		// EclipseJavaCompiler compiler = new EclipseJavaCompiler();
//		JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");
//		FileResourceReader pReader = new FileResourceReader(new File(sourceDir));
//		FileResourceStore pStore = new FileResourceStore(new File(targetDir));
//
//		ReloadingClassLoader classloader = new ReloadingClassLoader(this.getClass().getClassLoader());
//		ReloadingListener listener = new ReloadingListener();
//
//		listener.addReloadNotificationListener(classloader);
//
//		// FilesystemAlterationMonitor fam = new FilesystemAlterationMonitor();
//		// fam.addListener(new File(sourceDir), listener);
//		// fam.start();
//
//		CompilationResult result = compiler.compile(new String[] { "Simple.java" }, pReader, pStore);
//		CompilationProblem[] errors = result.getErrors();
//		System.out.println(errors.length + " errors");
//		for (CompilationProblem p : errors) {
//			System.err.print(p.getFileName() + " message : ");
//			System.err.println(p.getMessage());
//
//		}
//
//		try {
//			Class.forName("Simple");
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(result.getWarnings().length + " warnings");
//
//	}
}
