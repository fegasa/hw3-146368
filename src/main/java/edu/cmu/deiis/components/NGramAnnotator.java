package edu.cmu.deiis.components;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import edu.cmu.deiis.types.*;
import java.util.Iterator;
/* This class takes the tokens and answers generated from 
 * previous steps in the pipeline and makes uni, bi and trigrams 
 * correspondingly. Those items are stored in an FSArray, so each
 * NGram represents a set of unigrams or bigrams or trigrams for 
 * each answer.
 */

public class NGramAnnotator extends JCasAnnotator_ImplBase {	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

//Get Tokens
Iterator<?> tokens = aJCas.getJFSIndexRepository().getAllIndexedFS(Token.type);
int i = 0;
int[][] conjunto = new int[200][2];
while(tokens.hasNext()){
	Token tk = (Token)tokens.next();
	conjunto[i][0] = tk.getBegin();
	conjunto[i][1] = tk.getEnd();
	i++;
}
//Get Answers
Iterator<?> answers = aJCas.getJFSIndexRepository().getAllIndexedFS(Answer.type);
int j = 0;
int[][] answers_a = new int[200][2];
while(answers.hasNext()){
	Answer ans = (Answer)answers.next();
	answers_a[j][0] = ans.getBegin();
	answers_a[j][1] = ans.getEnd();
	j++;
}
//Unigrams

FSArray datos = null;
for(j=0;j<answers_a.length;j++){
datos = new FSArray(aJCas, tokensinansw(conjunto,answers_a[j][0],answers_a[j][1]));
int index =0;
NGram unigram = new NGram (aJCas, answers_a[j][0],answers_a[j][1]);
for(i=0;i<conjunto.length; i++){
	if((conjunto[i][1]<=answers_a[j][1])&&(conjunto[i][0]>=answers_a[j][0])){
	Annotation v = new Annotation (aJCas, conjunto[i][0] , conjunto[i][1]);
	v.setCasProcessorId(this.getClass().getName());
	v.setConfidence(1.0d);
	datos.set(index, v);
	index++;
	}
}
unigram.setElements(datos);
unigram.setElementType("unigram");
unigram.addToIndexes();
}

//Bigrams
int gram_st = 0;
int gram_end = 0;
FSArray datos_b = null;
for(int jb=0;jb<answers_a.length;jb++){
int tks = tokensinansw(conjunto,answers_a[jb][0],answers_a[jb][1]);
datos_b = new FSArray(aJCas, tks-1);
NGram bigram = new NGram (aJCas, answers_a[jb][0],answers_a[jb][1]);
int index = 0;
int[] answ_tok = tokens_a(conjunto,answers_a[jb][0],answers_a[jb][1]);
gram_st = answers_a[jb][0];
for(int g=0;g<tks-1;g++){
	if(g==0){
		gram_end = conjunto[answ_tok[g+1]][1];
	}else{
		gram_st = conjunto[answ_tok[g]][0];
		gram_end = conjunto[answ_tok[g+1]][1];
	}
	Annotation v_b = new Annotation (aJCas, gram_st , gram_end);
	v_b.setCasProcessorId(this.getClass().getName());
	v_b.setConfidence(1.0d);
	datos_b.set(index, v_b);
	index++;
}
bigram.setElements(datos_b);
bigram.setElementType("bigram");
bigram.addToIndexes();
}

//Tri
int tgram_st = 0;
int tgram_end = 0;
FSArray datos_t = null;
for(int jt=0;jt<answers_a.length;jt++){
int tks = tokensinansw(conjunto,answers_a[jt][0],answers_a[jt][1]);
datos_t = new FSArray(aJCas, tks-2);
NGram trigram = new NGram (aJCas, answers_a[jt][0],answers_a[jt][1]);
int tindex = 0;
int[] answ_tok = tokens_a(conjunto,answers_a[jt][0],answers_a[jt][1]);
tgram_st = answers_a[jt][0];
for(int g=0;g<tks-2;g++){
	if(g==0){
		tgram_end = conjunto[answ_tok[g+2]][1];
	}else{
		tgram_st = conjunto[answ_tok[g]][0];
		tgram_end = conjunto[answ_tok[g+2]][1];
	}
	Annotation v_t = new Annotation (aJCas, tgram_st , tgram_end);
	v_t.setCasProcessorId(this.getClass().getName());
	v_t.setConfidence(1.0d);
	datos_t.set(tindex, v_t);
	tindex++;
}
trigram.setElements(datos_t);
trigram.setElementType("trigram");
trigram.addToIndexes();
}

}
	public int tokensinansw(int[][] token_ar,int begin, int end){
		int count = 0;
		for(int i=0;i<token_ar.length;i++){
			if((token_ar[i][0]>=begin)&&(token_ar[i][1]<=end)){
				count++;
			}
		}
		return count;
	}
	public int[] tokens_a(int[][] token_ar,int begin, int end){
		int[] token_p = new int[tokensinansw(token_ar,begin,end)];
		int index = 0;
		for(int i=0;i<token_ar.length;i++){
			if((token_ar[i][0]>=begin)&&(token_ar[i][1]<=end)){
				token_p[index] = i;
				index++;
			}
		}
		return token_p;
	}


}