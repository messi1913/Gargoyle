/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : pak
 *	작성일   : 2016. 4. 22.
 *	작성자   : KYJ
 *******************************/
package org.mdkt.compiler;

/**
 * @author KYJ
 *
 */

class ExampleParent {

}

public class ExampleRef extends ExampleParent {

	public void hello() {
		System.out.println("ExampleRef www hello");
	}

	public static void main(String[] args) {
		new ExampleRef().hello();
	}
}
