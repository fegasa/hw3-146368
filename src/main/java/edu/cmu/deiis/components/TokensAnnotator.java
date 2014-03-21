package edu.cmu.deiis.components;


import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

import java.text.BreakIterator;
import java.text.ParsePosition;
import java.util.Locale;


public class TokensAnnotator extends JCasAnnotator_ImplBase {

	  static abstract class Maker {
	    abstract Annotation newAnnotation(JCas jcas, int start, int end);
	  }

	  JCas jcas;

	  String input;

	  ParsePosition pp = new ParsePosition(0);

	  // ****************************************
	  // * Static vars holding break iterators
	  // ****************************************
	  static final BreakIterator wordBreak = BreakIterator.getWordInstance(Locale.US);

	  // *********************************************
	  // * function pointers for new instances *
	  // *********************************************

	  static final Maker tokenAnnotationMaker = new Maker() {
	    Annotation newAnnotation(JCas jcas, int start, int end) {
	      return new Token(jcas, start, end);
	    }
	  };

	  // *************************************************************
	  // * process *
	  // *************************************************************
	  public void process(JCas aJCas) throws AnalysisEngineProcessException {
	    jcas = aJCas;
	    input = jcas.getDocumentText();

	    // Create Annotations
	    makeAnnotations(tokenAnnotationMaker, wordBreak);
	  }

	  // *************************************************************
	  // * Helper Methods *
	  // *************************************************************
	  void makeAnnotations(Maker m, BreakIterator b) {
	    b.setText(input);
	    for (int end = b.next(), start = b.first(); end != BreakIterator.DONE; start = end, end = b
	            .next()) {	    	
	      // eliminate all-whitespace tokens
	      boolean isWhitespace = true;
	      for (int i = start; i < end; i++) {
	        if (!Character.isWhitespace(input.charAt(i))) {
	          isWhitespace = false;
	          break;
	        }
	      }
	      if (!isWhitespace) {
	    	  if(start == 0){
	    	  }else if(start<(input.length()-3)){
if((input.substring(start, start+3).contains("A 1"))||(input.substring(start, start+3).contains("A 0"))){
}else if((input.substring(start, start+2).contains("0 "))||(input.substring(start, start+2).contains("1 "))){
}else{
	Token annotation_a = new Token(jcas, start, end);
    annotation_a.setCasProcessorId(this.getClass().getName());
    annotation_a.setConfidence(1.0d);
    annotation_a.addToIndexes();
    }
	    	  }else{

	    			Token annotation_a = new Token(jcas, start, end);
	    		    annotation_a.setCasProcessorId(this.getClass().getName());
	    		    annotation_a.setConfidence(1.0d);
	    		    annotation_a.addToIndexes();	    	  }
	      }
	    }
	  }
	}

