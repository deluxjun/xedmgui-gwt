package com.speno.xedm.gui.common.client.util;


public class regex {
	public static boolean patten_korean(String input)
	{
		final int HANGUL_UNICODE_START = 0xAC00;
		final int HANGUL_UNICODE_END = 0xD7AF;
		
		final int HANGUL_UNICODE_MOEUM_START = 12593;
		final int HANGUL_UNICODE_MOEUM_END = 12643;
	
		boolean check = false;
		for (int i = 0; i < input.length();  i++)
		{			
			int syllable = input.charAt(i);		
			if ((HANGUL_UNICODE_START  <= syllable) && (syllable <= HANGUL_UNICODE_END)){
				check = true;
			}
			else if((HANGUL_UNICODE_MOEUM_START   <= syllable) && (syllable <= HANGUL_UNICODE_MOEUM_END ))	{				
				check = true;
			}
		}
		return check;
	}
	public static Boolean patten_Schar(String input)
	{
		boolean check = false;
		char[] a = new char[]{'`','!','@','#','$','%','^','&','*','(',')','-','+','=','|','\\','{','}','[',']',':',':','"','\'',',','<','.','>','?','/',' ','~'};
		int[]  b = new int[a.length];
		for (int i = 0; i < input.length();  i++)
		{	
			for(int j = 0;j < a.length;j++)
			{
				b[j] = a[j];
				int syllable = input.charAt(i);		
				if(syllable == b[j])
				check = true;
			}
		}
		return check;		
	}	
}
