/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 7. 18.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

/**
 * @author KYJ
 *
 */
public class XMLFormatter implements Formatter {

	/**
	 * 
	 */
	public XMLFormatter() {
		
	}
	
	public static void main(String[] args) {
		
		StringBuffer sb = new StringBuffer();
//		sb.append("<MT_PP0032_LIMStoMESAsync_S xmlns=\"http://samsungbiologics.com/PP\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
//		sb.append("<requestString xmlns=\"\"><TransactionRequest><Header><PlantId></PlantId><SourceSystem>LIMS</SourceSystem><DestinationSystem>MES</DestinationSystem><TransactionId>0A3235DA-D1A2-4DDC-AA5C-5DDBAD3DD1DD</TransactionId><TransactionType>SampleInformationUpdate</TransactionType></Header><Data><BatchNo>3104367</BatchNo><BatchType>Routine</BatchType><Samples><Sample><SampleId>B00033010003</SampleId><SampleDescription>AEX Equilibration Buffer v2</SampleDescription><SampleFieldCode>S1B12FT</SampleFieldCode><SampleStatus>Prelogged</SampleStatus><Inventory><InventoryId>2301954</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>NovaSeptum Syringe</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Retain</PurposeOfSample></Inventory><Inventory><InventoryId>2301955</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>NovaSeptum Syringe</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Retain</PurposeOfSample></Inventory><Inventory><InventoryId>2301956</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>NovaSeptum Syringe</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Endotoxin</PurposeOfSample></Inventory><Inventory><InventoryId>2301957</InventoryId><TargetVolumeAmount>100</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>NovaSeptum Bag</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Spec Scan Blank</PurposeOfSample></Inventory><Inventory><InventoryId>2301958</InventoryId><TargetVolumeAmount>100</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>NovaSeptum Bag</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Spec Scan Blank</PurposeOfSample></Inventory></Sample><Sample><SampleId>B00033010004</SampleId><SampleDescription>UFDF Conditioning Buffer v2</SampleDescription><SampleFieldCode>S1B14PF</SampleFieldCode><SampleStatus>Started</SampleStatus><Inventory><InventoryId>2301963</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>Polystyrene Tube</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Endotoxin</PurposeOfSample></Inventory><Inventory><InventoryId>2301964</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>Polystyrene Tube</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Retain</PurposeOfSample></Inventory><Inventory><InventoryId>2301965</InventoryId><TargetVolumeAmount>5</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>Polystyrene Tube</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Retain</PurposeOfSample></Inventory><Inventory><InventoryId>2301966</InventoryId><TargetVolumeAmount>100</TargetVolumeAmount><TargetVolumeUOM>mL</TargetVolumeUOM><ContainerDescription>Polystyrene Bottle</ContainerDescription><StorageTemperature>2-8C</StorageTemperature><PurposeOfSample>Aliquot</PurposeOfSample></Inventory></Sample></Samples></Data></TransactionRequest></requestString>\n");
//		sb.append("</MT_PP0032_LIMStoMESAsync_S>\n");
		
		String format = new XMLFormatter().format(sb.toString());
		System.out.println(format);
		
		
//		System.out.println(ValueUtil.removeSpecialCharacter(format));
	}

	@Override
	public String format(String str) {

		int length = str.length();
		int depth = -1;
		boolean tagStart = false;
		boolean tagEnd = true;
		boolean isContentArea = false;

		boolean isMetaTag = false;

		boolean isDoubleQuoteBlockStart = false;
		boolean isDoubleQuoteBlockEnd = false;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {

			char currentC = str.charAt(i);
			
			//17.8.24 줄바꿈 제거.
			if(currentC == '\n')
				continue;
			
			if ('<' == currentC) {
				tagStart = true;
				tagEnd = false;

				if (i != length) {
					char nextChar = str.charAt(i + 1);
					if ('/' == nextChar) {
						if (!isDoubleQuoteBlockStart) {
							depth--;
						}
					} else if ('?' == nextChar) {
						depth--;
					}
				}
				if(i != 0)
					sb.append(System.lineSeparator());
				
				sb.append(getEmptyString(depth));

				sb.append(currentC);

				depth++;
				continue;
			}

			if ('>' == currentC) {
				tagStart = false;
				tagEnd = true;
				sb.append(currentC);

				continue;
			}

			if (tagStart) {

				if ('/' == currentC) {

					if (!isDoubleQuoteBlockStart) {
						depth--;
						sb.append(currentC);
						continue;
					}

					sb.append(currentC);
					continue;
				}

			}

			if (!isDoubleQuoteBlockStart && tagStart && '"' == currentC) {
				isDoubleQuoteBlockStart = true;
				isDoubleQuoteBlockEnd = false;
				sb.append(currentC);
				continue;
			}

			if (isDoubleQuoteBlockStart && tagStart && '"' == currentC) {
				isDoubleQuoteBlockStart = false;
				isDoubleQuoteBlockEnd = true;
				sb.append(currentC);
				continue;
			}

			//data block start
			if (tagEnd && isContentArea) {
				sb.append(System.lineSeparator());
				//17.8.24 탭제거
				sb.append(/* "\t" +*/ getEmptyString(depth));
			}

			// 태그 시작부터 내용이 기입
			if (tagStart && !tagEnd) {
				isContentArea = true;
				sb.append(currentC);
				continue;
				//태그 끝
			} else if (!tagStart && tagEnd) {
				isContentArea = false;
				sb.append(currentC);
				continue;
			}

		}

		return sb.toString();
	}

	public String getEmptyString(int depth) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < depth; i++)
			sb.append(" ");
		return sb.toString();
	}

	@Override
	public String toUpperCase(String source) {

		return null;
	}

	@Override
	public String toLowerCase(String source) {

		return null;
	}

	@Override
	public String split(String source, int position) {

		return null;
	}

}
