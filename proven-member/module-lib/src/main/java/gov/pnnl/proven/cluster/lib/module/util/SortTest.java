/*******************************************************************************
 * Copyright (c) 2017, Battelle Memorial Institute All rights reserved.
 * Battelle Memorial Institute (hereinafter Battelle) hereby grants permission to any person or entity 
 * lawfully obtaining a copy of this software and associated documentation files (hereinafter the 
 * Software) to redistribute and use the Software in source and binary forms, with or without modification. 
 * Such person or entity may use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and may permit others to do so, subject to the following conditions:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the 
 * following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Other than as used herein, neither the name Battelle Memorial Institute or Battelle may be used in any 
 * form whatsoever without the express written consent of Battelle.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * BATTELLE OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * General disclaimer for use with OSS licenses
 * 
 * This material was prepared as an account of work sponsored by an agency of the United States Government. 
 * Neither the United States Government nor the United States Department of Energy, nor Battelle, nor any 
 * of their employees, nor any jurisdiction or organization that has cooperated in the development of these 
 * materials, makes any warranty, express or implied, or assumes any legal liability or responsibility for 
 * the accuracy, completeness, or usefulness or any information, apparatus, product, software, or process 
 * disclosed, or represents that its use would not infringe privately owned rights.
 * 
 * Reference herein to any specific commercial product, process, or service by trade name, trademark, manufacturer, 
 * or otherwise does not necessarily constitute or imply its endorsement, recommendation, or favoring by the United 
 * States Government or any agency thereof, or Battelle Memorial Institute. The views and opinions of authors expressed 
 * herein do not necessarily state or reflect those of the United States Government or any agency thereof.
 * 
 * PACIFIC NORTHWEST NATIONAL LABORATORY operated by BATTELLE for the 
 * UNITED STATES DEPARTMENT OF ENERGY under Contract DE-AC05-76RL01830
 ******************************************************************************/
package gov.pnnl.proven.cluster.lib.module.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;



public class SortTest {    
	
	static final String sample = "Hello there I'm doing jut fine how are you anyway";
	
	static TreeSet<String> treeSet = new TreeSet<>();
	static ArrayList<String> arrayList = new ArrayList<>();
	static Set<String> h1Set = new HashSet<>();
	static List<String> h1Array = new ArrayList<>();
	
    static String getRandomString() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += (char)(80 + random.nextInt(40));
        }
        return result;
    }

    static void testHTreeSet() {
        
        for (int i = 0; i < 5000; i++) {
            h1Array.add(getRandomString());
        }
        h1Array.add(sample);
    }
    
    static String findHTreeSetTest() {
    	treeSet = new TreeSet<String>(h1Array);
    	String ret = treeSet.tailSet(sample).iterator().next();
    	return ret;
    }
    
    static void testTreeSet() {
        
        for (int i = 0; i < 5000; i++) {
            treeSet.add(getRandomString());
        }
        treeSet.add(sample);
    }
    
    static String findTreeSetTest() {
    	String ret = treeSet.tailSet(sample).iterator().next();
    	return ret;
    }
    
    static String findArrayListTest() {
    	return arrayList.get(arrayList.indexOf(sample));
    }

    static void testArrayList() {
        
        for (int i = 0; i < 5000; i++) {
            arrayList.add(getRandomString());
        }
        arrayList.add(sample);
        Collections.sort(arrayList);
    }

    public static void main(String[] args) {
        Date date1 = new Date();
        testTreeSet();
        Date date2 = new Date();
        System.out.println(date2.getTime() - date1.getTime());

        Date datea = new Date();
        testHTreeSet();
        Date dateb = new Date();
        System.out.println(dateb.getTime() - datea.getTime());
        
        Date date3 = new Date();
        testArrayList();
        Date date4 = new Date();
        System.out.println(date4.getTime() - date3.getTime());
        
        Date date5 = new Date();
        findTreeSetTest();
        Date date6 = new Date();
        System.out.println(date6.getTime() - date5.getTime());
        
        Date datec = new Date();
        findHTreeSetTest();
        Date dated = new Date();
        System.out.println(dated.getTime() - datec.getTime());

        Date date7 = new Date();
        findArrayListTest();
        Date date8 = new Date();
        System.out.println(date8.getTime() - date7.getTime());   
        
    }
}
