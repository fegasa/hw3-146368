package edu.cmu.deiis.components;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import edu.cmu.deiis.types.*;

/*
 * This class tags each test element as question or answer.
 * The '?' symbol is used as a delimiter for questions.
 */
public class TestElementAnnotator extends JCasAnnotator_ImplBase {	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String input = aJCas.getDocumentText();
		String[] lineas = input.split(System.getProperty("line.separator"));
		int indice = 0;
		for(int i=0; i<lineas.length; i++){
			String delimit = lineas[i].substring(0, 1);
		if(delimit.equals("Q")){
        	Question annotation = new Question(aJCas,indice+2,indice+lineas[i].indexOf("?"));
            annotation.setCasProcessorId(this.getClass().getName());
            annotation.setConfidence(1.0d);
            annotation.addToIndexes();
		}else if(delimit.equals("A")){
			Answer annotation_a = new Answer(aJCas, indice+4, indice+lineas[i].indexOf("."));
			annotation_a.setIsCorrect(lineas[i].charAt(2)=='1'?true:false);
            annotation_a.setCasProcessorId(this.getClass().getName());
            annotation_a.setConfidence(1.0d);
            annotation_a.addToIndexes();
		}
		indice += lineas[i].length()+1;
		}
		
	}
}
