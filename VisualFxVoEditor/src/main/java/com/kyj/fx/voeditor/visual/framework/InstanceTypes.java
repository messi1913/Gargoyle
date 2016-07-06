/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 5. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

/***************************
 *
 * FxUtil에서 컨트롤러를 생성하는 정책을 지정함. </br>
 *
 * <b>RequireNew</b>타입인경우 특별한 처리를 하지않음.</br>
 *
 * 일반적인 객체 생성방법과 동일함.</br>
 *
 * <b>Singleton</b>의 경우 FxMemory에 로드되는 객체를 관리하게된다.</br>
 *
 * <b> Singleton 발생되는 문제점 </br>
 * 객체는 하나만 관리되나 이미 로드된 씬을 다른 씬에 로드하는경우 한건만 로드된다는점에 주의해야함.
 *
 * 사용시
 * </b>
 *
 *
 * @author KYJ
 *
 ***************************/
public enum InstanceTypes {
	RequireNew, Singleton
}
