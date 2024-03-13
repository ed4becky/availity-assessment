package org.ed4becky;

public class LispValidatorApplication {
	public static void main(String[] args) {
		if(args == null || args.length == 0) {
			System.out.println("Missing string.");
			System.exit(-1);
		} else {
			int retCode = processString(args[0]);
			if (retCode == 0)
				System.out.println("Valid string.");
			else
				System.out.println("Invalid string.");
			System.exit(retCode);
		}
	}
	
	static int processString(String data){
		int cnt = 0;
		for(int i = 0; i < data.length(); i++) {
			if (data.charAt(i) == '(')
				cnt++;
			else if (data.charAt(i) == ')')
				cnt--;
			if(cnt < 0) return -1;	
		}
	return cnt == 0?0:-1;
	}
}
