package org.jpef.example;

import org.jpef.parallel.Joiner;


public class IntSumJoiner implements Joiner<Integer> {

	public Integer join(Integer first, Integer second) {
		return first + second;
	}
}
