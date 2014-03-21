package edu.cmu.deiis.components;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Question;
/*
 * This code gets the answers and scores, then orders the scored answers
 * with a comparator on descending order, and prints the order to the screen.
 * After that, TOP-n precision is calculated, and shown on screen.
 */
public class Evaluator extends JCasAnnotator_ImplBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String input = aJCas.getDocumentText();
		Iterator<?> question = aJCas.getJFSIndexRepository().getAllIndexedFS(Question.type);
		Question q = (Question)question.next();
		String quest = input.substring(q.getBegin(),q.getEnd());
		
		//Get Answers
		Iterator<?> answers = aJCas.getJFSIndexRepository().getAllIndexedFS(Answer.type);
		int numans=0;
		int correct_ans = 0;
		
		while(answers.hasNext()){
			Answer ans = (Answer)answers.next();
			if(ans.getBegin()!=0){
				numans++;
			}
			if(ans.getIsCorrect()){
				correct_ans++;
			}
		}
		Object[][] answers_scores= new Object[numans][3];
		int it = 0;
		Iterator<?> answers_Score = aJCas.getJFSIndexRepository().getAllIndexedFS(AnswerScore.type);
		while(answers_Score.hasNext()){
			AnswerScore as = (AnswerScore)answers_Score.next();
			answers_scores[it][1]=(String)as.getAnswer().getCoveredText();
			answers_scores[it][0]=(double)as.getScore();
			answers_scores[it][2]=(boolean)as.getAnswer().getIsCorrect();
			it++;
		}
		double hits = 0;

		List<Object[]> sublist = Arrays.asList(answers_scores);
		Collections.sort(sublist, new Comparator<Object[]>() {
		    @Override
		    public int compare(Object[] a1, Object[] a2) {
		        return -(Float.valueOf(a1[0].toString()).compareTo(Float.valueOf(a2[0].toString())));
		    }
		});		
		System.out.println("Q: "+quest+"?");
		for(int i=0; i<answers_scores.length;i++){
			
			System.out.println("Score: "+answers_scores[i][0]+" ("+answers_scores[i][1]+")");
		}
		System.out.println("");
	//TOP N Precision
		for(int i=0;i<correct_ans;i++){
			if(Boolean.parseBoolean(answers_scores[i][2].toString())){
				hits++;
			}
		}
		System.out.println("Precision @ " + correct_ans + ": " + (double)hits/(double)correct_ans);
		System.out.println("");
	}
	
	
}
