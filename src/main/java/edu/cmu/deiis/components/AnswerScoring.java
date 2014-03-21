package edu.cmu.deiis.components;

import java.util.Iterator;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Question;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import edu.cmu.deiis.types.*;

public class AnswerScoring extends JCasAnnotator_ImplBase {	
   	public void process(JCas aJCas) throws AnalysisEngineProcessException {
	//Get question
		String input = aJCas.getDocumentText();
		Iterator<?> question = aJCas.getJFSIndexRepository().getAllIndexedFS(Question.type);
		Question q = (Question)question.next();
		String quest = input.substring(q.getBegin(),q.getEnd());
	//Get Answers
	Iterator<?> answers = aJCas.getJFSIndexRepository().getAllIndexedFS(Answer.type);
	int j = 0;
	int numans = 0;
	while(answers.hasNext()){
		Answer ans = (Answer)answers.next();
		if(ans.getBegin()!=0){
			numans++;
		}
	}
	int[][] answers_a = new int[numans][2];
	while(answers.hasNext()){
		Answer ans = (Answer)answers.next();
		answers_a[j][0] = ans.getBegin();
		answers_a[j][1] = ans.getEnd();
		j++;
	}
	//Get NGrams
	Iterator<?> ngrams = aJCas.getJFSIndexRepository().getAllIndexedFS(NGram.type);
	Iterator<?> ngrams2 = aJCas.getJFSIndexRepository().getAllIndexedFS(NGram.type);

	int tot_u = 0;
	int tot_b = 0;
	int tot_t = 0;
	while(ngrams2.hasNext()){
		NGram gram2 = (NGram)ngrams2.next();
		if(gram2.getElementType()=="unigram"){
			tot_u++;
		}else if(gram2.getElementType()=="bigram"){
			tot_b++;
		}else if(gram2.getElementType()=="trigram"){
			tot_t++;
		}
	}

	String[][] a_unigrams = new String[tot_u][2];
	String[][] a_bigrams = new String[tot_b][2];
	String[][] a_trigrams = new String[tot_t][2];

	FSArray datos = null;
	int uni_count = 0;
	int bi_count = 0;
	int tri_count = 0;
	int end_gram = 0;
	int answ_ref_u=1;
	int answ_ref_b=1;
	int answ_ref_t=1;
	while(ngrams.hasNext()){
		NGram gram = (NGram)ngrams.next();
		datos = gram.getElements();
		end_gram = gram.getEnd();
		if(gram.getElementType()=="unigram"){
		for(int i=0;i<datos.size();i++){
			int x = gram.getElements(i).getBegin();
			if(x!=0){
				a_unigrams[uni_count][0] = input.substring((gram.getElements(i).getBegin()), (gram.getElements(i).getEnd()));
		    	a_unigrams[uni_count][1] = Integer.toString(answ_ref_u);
			    uni_count++;
			    if(gram.getElements(i).getEnd() == end_gram){
			    	answ_ref_u++;
			    }
			    
			}
		
		}
		//Similarity of this answer with the question using unigrams
		}else if(gram.getElementType()=="bigram"){
			datos = gram.getElements();
			for(int i=0;i<datos.size();i++){
				int x = gram.getElements(i).getBegin();
				if(x!=0){
					a_bigrams[bi_count][0] = input.substring((gram.getElements(i).getBegin()), (gram.getElements(i).getEnd()));
					a_bigrams[bi_count][1] = Integer.toString(answ_ref_b);
					bi_count++;
				    if(gram.getElements(i).getEnd() == end_gram){
				    	answ_ref_b++;
				    }
				}
			}
		}else if(gram.getElementType()=="trigram"){
			datos = gram.getElements();
			for(int i=0;i<datos.size();i++){
				int x = gram.getElements(i).getBegin();
				if(x!=0){
					a_trigrams[tri_count][0] = input.substring((gram.getElements(i).getBegin()), (gram.getElements(i).getEnd()));
				    a_trigrams[tri_count][1] = Integer.toString(answ_ref_t);
				    tri_count++;
				    if(gram.getElements(i).getEnd() == end_gram){
				    	answ_ref_t++;
				    }
				}
		}
	}
	}
//SIMILARITY

	int ansel = 1;
double[][] scored_ans = new double[answers_a.length][2];
	for(int i=0;i<answers_a.length;i++){
		scored_ans[i][1] = similarity(quest,a_unigrams,ansel)*0.2+similarity(quest,a_bigrams,ansel)*0.3+similarity(quest,a_trigrams,ansel)*0.5;
		scored_ans[i][0] = ansel;
		ansel++;
	}
	Iterator<?> answers2 = aJCas.getJFSIndexRepository().getAllIndexedFS(Answer.type);
	int itans = 0;
	while(answers2.hasNext()){
		Answer ans = (Answer)answers2.next();
		AnswerScore annotation = new AnswerScore(aJCas,ans.getBegin(),ans.getEnd());
        annotation.setCasProcessorId(this.getClass().getName());
        annotation.setAnswer(ans);
        annotation.setScore((double)scored_ans[itans][1]);
        annotation.setConfidence(1.0d);
        annotation.addToIndexes();
        itans++;
	}
	}

private double similarity(String question, String[][] answer, int answ){
double points = 0.0;
String[] qgrams = question.split(" ");
double possible = (double)(qgrams.length*0.2+(qgrams.length-1)*0.3+(qgrams.length-2)*0.5);
double last = 0.0;
for(int i=0;i<answer.length;i++){
	if((answer[i][1]!=null)&&(answer[i][1].contains(Integer.toString(answ)))){
	if(question.contains(answer[i][0])){
		points++;
	}
	}
}
if(possible!=0.0){
	last = points;
}
return last;
}
}
