package com.seta.android.db;

public class WordsDb {
	private int wordsId;
	private String errorWords;
	private String correctWords;
	private String errorWordsFirstpy;
	private String errorWordsSimplepy;
	private String errorWordsAllpy;
	private String correctWordsFirstpy;
	private String correctWordsSimplepy;
	private String correctWordsAllpy;

	public WordsDb() {
		this.wordsId = 0;
		this.errorWords = "";
		this.errorWordsAllpy = "";
		this.errorWordsSimplepy = "";
		this.correctWords = "";
		this.correctWordsAllpy = "";
		this.correctWordsSimplepy = "";
	}

	public int getWordsId() {
		return wordsId;
	}

	public void setWordsId(int wordsId) {
		this.wordsId = wordsId;
	}

	public String getErrorWords() {
		return errorWords;
	}

	public void setErrorWords(String errorWords) {
		this.errorWords = errorWords;
	}

	public String getCorrectWords() {
		return correctWords;
	}

	public void setCorrectWords(String correctWords) {
		this.correctWords = correctWords;
	}

	public String getErrorWordsFirstpy() {
		return errorWordsFirstpy;
	}

	public void setErrorWordsFirstpy(String errorWordsFirstpy) {
		this.errorWordsFirstpy = errorWordsFirstpy;
	}

	public String getErrorWordsSimplepy() {
		return errorWordsSimplepy;
	}

	public void setErrorWordsSimplepy(String errorWordsSimplepy) {
		this.errorWordsSimplepy = errorWordsSimplepy;
	}

	public String getErrorWordsAllpy() {
		return errorWordsAllpy;
	}

	public void setErrorWordsAllpy(String errorWordsAllpy) {
		this.errorWordsAllpy = errorWordsAllpy;
	}

	public String getCorrectWordsFirstpy() {
		return correctWordsFirstpy;
	}

	public void setCorrectWordsFirstpy(String correctWordsFirstpy) {
		this.correctWordsFirstpy = correctWordsFirstpy;
	}

	public String getCorrectWordsSimplepy() {
		return correctWordsSimplepy;
	}

	public void setCorrectWordsSimplepy(String correctWordsSimplepy) {
		this.correctWordsSimplepy = correctWordsSimplepy;
	}

	public String getCorrectWordsAllpy() {
		return correctWordsAllpy;
	}

	public void setCorrectWordsAllpy(String correctWordsAllpy) {
		this.correctWordsAllpy = correctWordsAllpy;
	}

}
