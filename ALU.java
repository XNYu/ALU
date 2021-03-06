/**
 * 模拟ALU进行整数和浮点数的四则运算
 * 
 * @author [151250174_徐杨晨]
 *
 */

public class ALU {

	/**
	 * 生成十进制整数的二进制补码表示。<br/>
	 * 例：integerRepresentation("9", 8)
	 * 
	 * @param number
	 *            十进制整数。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length
	 *            二进制补码表示的长度
	 * @return number的二进制补码表示，长度为length
	 */
	public String integerRepresentation(String number, int length) {
		int decNumber = Integer.parseInt(number);
		String[] temp = new String[length];
		String result = new String();
		// 当十进制数为0时
		if (decNumber == 0) {
			for (int i = 0; i < length; i++) {
				temp[i] = "0";
			}
			for (int i = 0; i < length; i++) {
				result += temp[i];
			}
		}
		// 当十进制数大于0时
		else if (decNumber > 0) {
			temp[0] = "0";
			for (int i = 1; i < length; i++) {
				if (Math.pow(2, length - i - 1) <= decNumber) {
					temp[i] = "1";
					decNumber -= Math.pow(2, length - i - 1);
				} else {
					temp[i] = "0";
				}
			}
			for (int i = 0; i < length; i++) {
				result += temp[i];
			}
		}
		// 当十进制数小于0时
		else {
			temp[0] = "1";
			for (int i = 1; i < length; i++) {
				if ((-Math.pow(2, length - 1) + Math.pow(2, length - i - 1)) <= decNumber) {
					temp[i] = "1";
					decNumber -= Math.pow(2, length - i - 1);
				} else {
					temp[i] = "0";
				}
			}
			for (int i = 0; i < length; i++) {
				result += temp[i];
			}
		}
		return result;
	}

	/**
	 * 生成十进制浮点数的二进制表示。 需要考虑 0、反规格化、正负无穷（“+Inf”和“-Inf”）、 NaN等因素，具体借鉴 IEEE 754。
	 * 舍入策略为向0舍入。<br/>
	 * 例：floatRepresentation("11.375", 8, 11)
	 * 
	 * @param number
	 *            十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @return number的二进制表示，长度为 1+eLength+sLength。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String floatRepresentation(String number, int eLength, int sLength) {
		int totalLength = 1 + eLength + sLength;
		int base = 2;
		int bias = (int) (Math.pow(2, eLength - 1)) - 1;
		int exponent = 0;
		int dot = 0;
		String[] results = new String[totalLength];
		String result = "";
		String temp = "";
		// 初始化结果
		for (int i = 0; i < results.length; i++) {
			results[i] = "0";
		}

		// 检查输入是否为数字
		for (int i = 0; i < number.length(); i++) {
			if ((!Character.isDigit(number.charAt(i))) && (number.charAt(i) != '.') && (number.charAt(i) != '-')) {
				return "NaN";
			}
		}
		// 输入为0
		if (Double.parseDouble(number) == 0.0) {
			for (int i = 0; i < results.length; i++) {
				result += results[i];
			}
			return result;
		}
		// 负数
		if (number.charAt(0) == '-') {
			results[0] = "1";
		}

		String[] num = number.split("\\.");
		String dec = "0." + num[1];
		double d = Double.parseDouble(dec);
		int intNumber = Math.abs(Integer.parseInt(num[0]));
		int length = 0;
		for (int i = 0; i < eLength; i++) {
			if (intNumber >= (int) Math.pow(base, i)) {
				length = i + 1;
			}
		}
		temp += integerRepresentation(intNumber + "", length + 2).substring(1);
		totalLength -= temp.length();
		dot = temp.length();
		for (int i = 0; i < totalLength + 1; i++) {
			if ((d * 2) >= 1.0) {
				temp += "1";
				d = d * 2 - 1.0;
			} else {
				temp += "0";
				d = d * 2;
			}
		}
		int newdot = 0;
		for (int i = 0; i < totalLength; i++) {
			if (temp.charAt(i) == '1') {
				newdot = i + 1;
				break;
			}
		}
		// 指数上溢
		if ((dot - newdot + bias) > ((int) Math.pow(base, eLength) - 1)) {
			for (int i = 0; i < eLength; i++) {
				results[i + 1] = "1";
			}
			for (int i = eLength + 1; i < 1 + eLength + sLength; i++) {
				results[i] = "0";
			}
		}
		// 指数下溢,反规格化
		else if (dot - newdot + bias < 0) {
			exponent = -((int) Math.pow(base, eLength - 1) - 1);
			newdot = dot + bias - exponent;
			for (int i = 0; i < eLength; i++) {
				results[i + 1] = "0";
			}
			for (int i = eLength + 1; i < 1 + eLength + sLength; i++) {
				results[i] = temp.substring(newdot, newdot + 1);
				newdot++;
			}
		}
		// 规格化
		else {
			exponent = dot - newdot + bias;
			for (int i = 0; i < eLength; i++) {
				results[i + 1] = integerRepresentation(exponent + "", eLength + 1).substring(1).charAt(i) + "";
			}
			for (int i = eLength + 1; i < 1 + eLength + sLength; i++) {
				results[i] = temp.substring(newdot, newdot + 1);
				newdot++;
			}
		}

		// 生成字符串
		for (int i = 0; i < results.length; i++) {
			result += results[i];
		}
		return result;
	}

	/**
	 * 生成十进制浮点数的IEEE 754表示，要求调用{@link #floatRepresentation(String, int, int)
	 * floatRepresentation}实现。<br/>
	 * 例：ieee754("11.375", 32)
	 * 
	 * @param number
	 *            十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length
	 *            二进制表示的长度，为32或64
	 * @return number的IEEE 754表示，长度为length。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String ieee754(String number, int length) {
		String result = "";
		// 32位单精度
		if (length == 32) {
			result = floatRepresentation(number, 8, 23);
		}
		// 64位双精度
		else if (length == 64) {
			result = floatRepresentation(number, 11, 52);
		} else {
			result = "Not ieee754";
		}
		return result;
	}

	/**
	 * 计算二进制补码表示的整数的真值。<br/>
	 * 例：integerTrueValue("00001001")
	 * 
	 * @param operand
	 *            二进制补码表示的操作数
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 */
	public String integerTrueValue(String operand) {
		int result = 0;
		// 补码第一位为0，代表真值为正数或零
		if (operand.substring(0, 1).equals("0")) {
			for (int i = operand.length(); i > 0; i--) {
				if (operand.substring(i - 1, i).equals("1")) {
					result += Math.pow(2, operand.length() - i);
				}
			}
			return result + "";
		}
		// 补码第一位为1，代表真值为负数
		else {
			result = (int) -Math.pow(2, operand.length() - 1);
			for (int i = 1; i < operand.length(); i++) {
				if (operand.substring(i, i + 1).equals("1")) {
					result += Math.pow(2, operand.length() - i - 1);
				}
			}
			return result + "";
		}
	}

	/**
	 * 计算二进制原码表示的浮点数的真值。<br/>
	 * 例：floatTrueValue("01000001001101100000", 8, 11)
	 * 
	 * @param operand
	 *            二进制表示的操作数
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位。正负无穷分别表示为“+Inf”和“-Inf”，
	 *         NaN表示为“NaN”
	 */
	public String floatTrueValue(String operand, int eLength, int sLength) {
		String result = "";
		int base = 2;
		int bias = (int) Math.pow(base, eLength - 1) - 1;
		int exponent = 0;
		double temp = 0.0;
		// 切割浮点数
		String[] num = new String[3];
		num[0] = operand.substring(0, 1);
		num[1] = operand.substring(1, eLength + 1);
		num[2] = operand.substring(eLength + 1, operand.length());
		// 检查是否为NaN
		if ((!num[1].contains("0")) && (num[2].contains("1"))) {
			return "NaN";
		}
		// 检查是否为无穷
		else if ((!num[1].contains("0")) && (!num[2].contains("1"))) {
			if (num[0].equals("0")) {
				return "+inf";
			} else {
				return "-inf";
			}
		}
		// 检查是否为0
		else if ((!num[1].contains("1")) && (!num[2].contains("1"))) {
			return "0";
		}
		// 检查反规格化
		else if (!num[1].contains("1")) {
			if (num[0].equals("1")) {
				result += "-";
			}
			temp = 0.0;
			exponent = Integer.parseInt(integerTrueValue1(num[1])) - bias;
			for (int i = 0; i < sLength; i++) {
				if (num[2].charAt(i) == '1')
					temp += Math.pow(base, -i - 1);
			}
			temp = temp * Math.pow(base, exponent);
			result += temp + "";
		}
		// 规格化
		else {
			if (num[0].equals("1")) {
				result += "-";
			}
			temp = 1.0;
			exponent = Integer.parseInt(integerTrueValue1(num[1])) - bias;
			for (int i = 0; i < sLength; i++) {
				if (num[2].charAt(i) == '1')
					temp += Math.pow(base, -i - 1);
			}
			temp = temp * Math.pow(base, exponent);
			result += temp + "";
		}

		return result;
	}

	/**
	 * 按位取反操作。<br/>
	 * 例：negation("00001001")
	 * 
	 * @param operand
	 *            二进制表示的操作数
	 * @return operand按位取反的结果
	 */
	public String negation(String operand) {
		String result = "";
		for (int i = 0; i < operand.length(); i++) {
			result += notGate(operand.charAt(i));
		}
		return result;
	}

	/**
	 * 左移操作。<br/>
	 * 例：leftShift("00001001", 2)
	 * 
	 * @param operand
	 *            二进制表示的操作数
	 * @param n
	 *            左移的位数
	 * @return operand左移n位的结果
	 */
	public String leftShift(String operand, int n) {
		String[] op = new String[operand.length()];
		for (int i = 0; i < operand.length(); i++) {
			op[i] = operand.substring(i, i + 1);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < operand.length() - 1; j++) {
				op[j] = op[j + 1];
			}
		}
		for (int i = 0; i < n; i++) {
			op[operand.length() - i - 1] = "0";
		}
		operand = "";
		for (int i = 0; i < op.length; i++) {
			operand += op[i];
		}
		return operand;
	}

	/**
	 * 逻辑右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * 
	 * @param operand
	 *            二进制表示的操作数
	 * @param n
	 *            右移的位数
	 * @return operand逻辑右移n位的结果
	 */
	public String logRightShift(String operand, int n) {
		String[] op = new String[operand.length()];
		for (int i = 0; i < operand.length(); i++) {
			op[i] = operand.substring(i, i + 1);
		}
		for (int i = 0; i < n; i++) {
			for (int j = operand.length() - 1; j > 0; j--) {
				op[j] = op[j - 1];
			}
		}
		for (int i = 0; i < n; i++) {
			op[i] = "0";
		}
		operand = "";
		for (int i = 0; i < op.length; i++) {
			operand += op[i];
		}
		return operand;
	}

	/**
	 * 算术右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * 
	 * @param operand
	 *            二进制表示的操作数
	 * @param n
	 *            右移的位数
	 * @return operand算术右移n位的结果
	 */
	public String ariRightShift(String operand, int n) {
		String[] op = new String[operand.length()];
		for (int i = 0; i < operand.length(); i++) {
			op[i] = operand.substring(i, i + 1);
		}
		for (int i = 0; i < n; i++) {
			for (int j = operand.length() - 1; j > 0; j--) {
				op[j] = op[j - 1];
			}
		}
		if (operand.charAt(0) == '0') {
			for (int i = 0; i < n; i++) {
				op[i] = "0";
			}
		} else {
			for (int i = 0; i < n; i++) {
				op[i] = "1";
			}
		}

		operand = "";
		for (int i = 0; i < op.length; i++) {
			operand += op[i];
		}
		return operand;
	}

	/**
	 * 全加器，对两位以及进位进行加法运算。<br/>
	 * 例：fullAdder('1', '1', '0')
	 * 
	 * @param x
	 *            被加数的某一位，取0或1
	 * @param y
	 *            加数的某一位，取0或1
	 * @param c
	 *            低位对当前位的进位，取0或1
	 * @return 相加的结果，用长度为2的字符串表示，第1位表示进位，第2位表示和
	 */
	public String fullAdder(char x, char y, char c) {
		String result = "";
		String S = xorGate(xorGate(x, y), c) + "";
		String C = orGate(andGate(x, y), orGate(andGate(x, c), andGate(y, c))) + "";
		result = C + S;
		return result;
	}

	/**
	 * 4位先行进位加法器。要求采用{@link #fullAdder(char, char, char) fullAdder}来实现<br/>
	 * 例：claAdder("1001", "0001", '1')
	 * 
	 * @param operand1
	 *            4位二进制表示的被加数
	 * @param operand2
	 *            4位二进制表示的加数
	 * @param c
	 *            低位对当前位的进位，取0或1
	 * @return 长度为5的字符串表示的计算结果，其中第1位是最高位进位，后4位是相加结果，其中进位不可以由循环获得
	 */
	public String claAdder(String operand1, String operand2, char c) {
		String result = "";
		char x1 = operand1.charAt(3);
		char x2 = operand1.charAt(2);
		char x3 = operand1.charAt(1);
		char x4 = operand1.charAt(0);
		char y1 = operand2.charAt(3);
		char y2 = operand2.charAt(2);
		char y3 = operand2.charAt(1);
		char y4 = operand2.charAt(0);
		char p1 = orGate(x1, y1);
		char p2 = orGate(x2, y2);
		char p3 = orGate(x3, y3);
		char p4 = orGate(x4, y4);
		char g1 = andGate(x1, y1);
		char g2 = andGate(x2, y2);
		char g3 = andGate(x3, y3);
		char g4 = andGate(x4, y4);
		char c1 = orGate(g1, andGate(p1, c));
		char c2 = orGate(orGate(g2, andGate(p2, g1)), andGate(andGate(p2, p1), c));
		char c3 = orGate(g3, orGate(andGate(p3, g2),
				orGate(andGate(p3, andGate(p2, g1)), andGate(p3, andGate(p2, andGate(p1, c))))));
		char c4 = orGate(g4,
				orGate(andGate(p4, g3),
						orGate(andGate(p4, andGate(p3, g2)), orGate(andGate(p4, andGate(p3, andGate(p2, g1))),
								andGate(p4, andGate(p3, andGate(p2, andGate(p1, c))))))));
		result = c4 + "" + fullAdder(x4, y4, c3).charAt(1) + "" + fullAdder(x3, y3, c2).charAt(1) + ""
				+ fullAdder(x2, y2, c1).charAt(1) + "" + fullAdder(x1, y1, c).charAt(1) + "";
		return result;
	}

	/**
	 * 加一器，实现操作数加1的运算。 需要采用与门、或门、异或门等模拟， 不可以直接调用
	 * {@link #fullAdder(char, char, char) fullAdder}、
	 * {@link #claAdder(String, String, char) claAdder}、
	 * {@link #adder(String, String, char, int) adder}、
	 * {@link #integerAddition(String, String, int) integerAddition}方法。<br/>
	 * 例：oneAdder("00001001")
	 * 
	 * @param operand
	 *            二进制补码表示的操作数
	 * @return operand加1的结果，长度为operand的长度加1，其中第1位指示是否溢出（溢出为1，否则为0），其余位为相加结果
	 */
	public String oneAdder(String operand) {
		String result = "";
		String temp = "";
		char S = '0';
		char C = '0';
		// 用于存放Cn-1，以判断是否溢出
		char CC = '0';

		String one = "";
		for (int i = 0; i < operand.length(); i++) {
			if (i == operand.length() - 1) {
				one += "1";
			} else {
				one += "0";
			}
		}

		for (int i = operand.length() - 1; i >= 0; i--) {
			S = xorGate(xorGate(operand.charAt(i), one.charAt(i)), C);
			C = orGate(andGate(operand.charAt(i), one.charAt(i)),
					orGate(andGate(operand.charAt(i), C), andGate(one.charAt(i), C)));
			if (i == 1) {
				CC = C;
			}
			temp = S + temp;
		}
		if (xorGate(C, CC) == '1') {
			result += "1";
			result += temp;
		} else {
			result += "0";
			result += temp;
		}
		return result;
	}

	/**
	 * 加法器，要求调用{@link #claAdder(String, String, char)}方法实现。<br/>
	 * 例：adder("0100", "0011", ‘0’, 8)
	 * 
	 * @param operand1
	 *            二进制补码表示的被加数
	 * @param operand2
	 *            二进制补码表示的加数
	 * @param c
	 *            最低位进位
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，
	 *            需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String adder(String operand1, String operand2, char c, int length) {
		String result = "";
		int claAdderNum = length / 4;
		// 补位
		int count = 0;
		String temp = "";
		if ((count = length - operand1.length()) > 0) {
			temp = operand1;
			if (operand1.charAt(0) == '0') {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "0";
				}
			} else {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "1";
				}
			}
			operand1 += temp;
		}
		if ((count = length - operand2.length()) > 0) {
			temp = operand2;
			if (operand2.charAt(0) == '0') {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "0";
				}
			} else {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "1";
				}
			}
			operand2 += temp;
		}

		for (int i = 0; i < claAdderNum; i++) {
			if (i == 0) {
				result = claAdder(operand1.substring(operand1.length() - 4 * (i + 1), operand1.length() - 4 * i),
						operand2.substring(operand2.length() - 4 * (i + 1), operand2.length() - 4 * i), c);
			} else {
				result = claAdder(operand1.substring(operand1.length() - 4 * (i + 1), operand1.length() - 4 * i),
						operand2.substring(operand2.length() - 4 * (i + 1), operand2.length() - 4 * i),
						result.charAt(0)) + result.substring(1);
			}
		}
		// 判断是否溢出
		if (orGate(andGate(andGate(operand1.charAt(0), operand2.charAt(0)), notGate(result.charAt(1))),
				andGate(andGate(notGate(operand1.charAt(0)), notGate(operand2.charAt(0))), result.charAt(1))) == '1') {
			result = "1" + result.substring(1);
		} else {
			result = "0" + result.substring(1);
		}
		return result;
	}

	/**
	 * 整数加法，要求调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerAddition("0100", "0011", 8)
	 * 
	 * @param operand1
	 *            二进制补码表示的被加数
	 * @param operand2
	 *            二进制补码表示的加数
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，
	 *            需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String integerAddition(String operand1, String operand2, int length) {
		return adder(operand1, operand2, '0', length);
	}

	/**
	 * 整数减法，可调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerSubtraction("0100", "0011", 8)
	 * 
	 * @param operand1
	 *            二进制补码表示的被减数
	 * @param operand2
	 *            二进制补码表示的减数
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，
	 *            需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相减结果
	 */
	public String integerSubtraction(String operand1, String operand2, int length) {
		// 判断是否溢出
		if ((oneAdder(negation(operand2)).charAt(0) == '1')) {
			operand2 = oneAdder(negation(operand2)).substring(1);
			return "1" + adder(operand1, operand2, '0', length).substring(1);
		} else {
			operand2 = oneAdder(negation(operand2)).substring(1);
			return adder(operand1, operand2, '0', length);
		}

	}

	/**
	 * 整数乘法，使用Booth算法实现，可调用{@link #adder(String, String, char, int) adder}等方法。
	 * <br/>
	 * 例：integerMultiplication("0100", "0011", 8)
	 * 
	 * @param operand1
	 *            二进制补码表示的被乘数
	 * @param operand2
	 *            二进制补码表示的乘数
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，
	 *            需要在高位补符号位
	 * @return 长度为length+1的字符串表示的相乘结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相乘结果
	 */
	public String integerMultiplication(String operand1, String operand2, int length) {
		String result = "";
		// 补位
		int count = 0;
		String temp = "";
		if ((count = length - operand1.length()) > 0) {
			temp = operand1;
			if (operand1.charAt(0) == '0') {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "0";
				}
			} else {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "1";
				}
			}
			operand1 += temp;
		}
		if ((count = length - operand2.length()) > 0) {
			temp = operand2;
			if (operand2.charAt(0) == '0') {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "0";
				}
			} else {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "1";
				}
			}
			operand2 += temp;
		}
		String X = operand1;
		String Y = operand2;
		// 结果置零
		for (int i = 0; i < operand1.length(); i++) {
			result += "0";
		}
		// 第二个操作数加一位
		Y += "0";
		result = result + Y;
		for (int i = Y.length() - 1; i > 0; i--) {
			if ((Y.charAt(i) - Y.charAt(i - 1)) == 0) {
				result = ariRightShift(result, 1);
			} else if ((Y.charAt(i) - Y.charAt(i - 1)) == 1) {
				result = integerAddition(result.substring(0, length), X, length).substring(1)
						+ result.substring(length);
				result = ariRightShift(result, 1);
			} else {
				result = integerSubtraction(result.substring(0, length), X, length).substring(1)
						+ result.substring(length);
				result = ariRightShift(result, 1);
			}
		}
		// 判断是否溢出
		if (result.substring(0, length).contains("1") && result.charAt(length) == '0') {
			result = "1" + result.substring(length, length << 1);
		} else if (result.substring(0, length).contains("0") && result.charAt(length) == '1') {
			result = "1" + result.substring(length, length << 1);
		} else {
			result = "0" + result.substring(length, length << 1);
		}
		return result;
	}

	/**
	 * 整数的不恢复余数除法，可调用{@link #adder(String, String, char, int) adder}等方法实现。<br/>
	 * 例：integerDivision("0100", "0011", 8)
	 * 
	 * @param operand1
	 *            二进制补码表示的被除数
	 * @param operand2
	 *            二进制补码表示的除数
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，
	 *            需要在高位补符号位
	 * @return 长度为2*length+1的字符串表示的相除结果，其中第1位指示是否溢出（溢出为1，否则为0），其后length位为商，
	 *         最后length位为余数
	 */
	public String integerDivision(String operand1, String operand2, int length) {
		String result = "";
		boolean isOverflow = false;
		// 补位
		int count = 0;
		String temp = "";
		if ((count = length - operand1.length()) > 0) {
			temp = operand1;
			if (operand1.charAt(0) == '0') {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "0";
				}
			} else {
				operand1 = "";
				for (int i = 0; i < count; i++) {
					operand1 += "1";
				}
			}
			operand1 += temp;
		}
		if ((count = length - operand2.length()) > 0) {
			temp = operand2;
			if (operand2.charAt(0) == '0') {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "0";
				}
			} else {
				operand2 = "";
				for (int i = 0; i < count; i++) {
					operand2 += "1";
				}
			}
			operand2 += temp;
		}
		String divisor = operand2;
		String quotient = operand1;
		String remainder = "";
		// 判断除数是否为0
		if (!operand2.contains("1")) {
			if (operand1.contains("1")) {
				return "exception";
			} else {
				return "NaN";
			}
		}
		// 判断除数与被除数是否相等
		if (operand1.equals(operand2)) {
			quotient = "";
			for (int i = 0; i < length; i++) {
				remainder += "0";
				if (i == length - 1) {
					quotient += "1";
				} else {
					quotient += "0";
				}
			}
			result = quotient + remainder;
			return "0" + result;
		}

		if (quotient.charAt(0) == '0') {
			for (int i = 0; i < length; i++) {
				remainder += "0";
			}
		} else {
			for (int i = 0; i < length; i++) {
				remainder += "1";
			}
		}
		// 同号相减，异号相加
		if (xorGate(remainder.charAt(0), divisor.charAt(0)) == '0') {
			remainder = integerSubtraction(remainder, divisor, length).substring(1);
		} else {
			remainder = integerAddition(remainder, divisor, length).substring(1);
		}
		temp = remainder + quotient;
		if (xorGate(remainder.charAt(0), divisor.charAt(0)) == '0') {
			temp += "1";
		} else {
			temp += "0";
		}
		boolean isAdd = true;
		for (int i = 0; i < length; i++) {
			String remainder1 = temp.substring(0, length);
			if (xorGate(remainder1.charAt(0), divisor.charAt(0)) == '0') {
				isAdd = false;
			}
			temp = leftShift(temp, 1).substring(0, length * 2);
			remainder = temp.substring(0, length);
			quotient = temp.substring(length);
			// 异号相除，当余数在加减之后为0时，需将0看成与除数正负号相反的数
			// 同号相除，将0看成与除数正负号相同的数
			if ((!remainder.contains("1")) && (xorGate(operand1.charAt(0), operand2.charAt(0)) == '1')) {
				remainder = integerAddition(remainder, divisor, length).substring(1);
			} else if ((!remainder.contains("1")) && (xorGate(operand1.charAt(0), operand2.charAt(0)) == '0')) {
				remainder = integerSubtraction(remainder, divisor, length).substring(1);
			} else if (isAdd == false) {
				remainder = integerSubtraction(remainder, divisor, length).substring(1);
			} else {
				remainder = integerAddition(remainder, divisor, length).substring(1);
			}
			temp = remainder + quotient;
			if ((!remainder.contains("1")) && (xorGate(operand1.charAt(0), operand2.charAt(0)) == '1')) {
				temp += "0";
			} else if ((!remainder.contains("1")) && (xorGate(operand1.charAt(0), operand2.charAt(0)) == '0')) {
				temp += "1";
			} else if (xorGate(remainder.charAt(0), divisor.charAt(0)) == '0') {
				temp += "1";
			} else {
				temp += "0";
			}
		}
		// 若余数与被除数异号
		// 当被除数与除数同号时，余数加除数
		// 当被除数与除数异号时，余数减除数
		if (xorGate(remainder.charAt(0), operand1.charAt(0)) == '1') {
			if (xorGate(operand1.charAt(0), operand2.charAt(0)) == '0') {
				remainder = integerAddition(remainder, divisor, length).substring(1);
			} else {

				remainder = integerSubtraction(remainder, divisor, length).substring(1);
			}
		}
		// 商为负数需加一
		quotient = leftShift(temp.substring(length), 1).substring(0, length);
		if (quotient.charAt(0) == '1') {
			quotient = oneAdder(quotient).substring(1);
		}
		result = quotient + remainder;

		if (isOverflow) {
			return "1" + result;
		} else {
			return "0" + result;
		}

	}

	/**
	 * 带符号整数加法，可以调用{@link #adder(String, String, char, int) adder}等方法，
	 * 但不能直接将操作数转换为补码后使用{@link #integerAddition(String, String, int)
	 * integerAddition}、 {@link #integerSubtraction(String, String, int)
	 * integerSubtraction}来实现。<br/>
	 * 例：signedAddition("1100", "1011", 8)
	 * 
	 * @param operand1
	 *            二进制原码表示的被加数，其中第1位为符号位
	 * @param operand2
	 *            二进制原码表示的加数，其中第1位为符号位
	 * @param length
	 *            存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度（不包含符号），当某个操作数的长度小于length时，
	 *            需要将其长度扩展到length
	 * @return 长度为length+2的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），第2位为符号位，
	 *         后length位是相加结果
	 */
	public String signedAddition(String operand1, String operand2, int length) {
		String result = "";
		boolean isOverflow = false;
		int length1 = operand1.length();
		int length2 = operand2.length();
		String sign1 = operand1.substring(0, 1);
		String sign2 = operand2.substring(0, 1);
		String op1 = operand1;
		String op2 = operand2;
		char c = '0';
		// 补位
		operand1 = "0" + operand1.substring(1);
		operand2 = "0" + operand2.substring(1);
		for (int i = 0; i < (length - length1); i++) {
			if (i == (length - length1 - 1) && op1.charAt(0) == '1') {
				operand1 = "1" + operand1;
			} else {
				operand1 = "0" + operand1;
			}
		}
		for (int i = 0; i < (length - length2); i++) {
			if (i == (length - length2 - 1) && op2.charAt(0) == '1') {
				operand2 = "1" + operand2;
			} else {
				operand2 = "0" + operand2;
			}
		}
		// 符号位相同
		if (xorGate(op1.charAt(0), op2.charAt(0)) == '0') {
			operand1 = operand1.substring(1);
			operand2 = operand2.substring(1);
			for (int i = 0; i < operand1.length(); i++) {
				if (i == operand1.length() - 1) {
					result = fullAdder(operand1.charAt(operand1.length() - i - 1),
							operand2.charAt(operand2.length() - i - 1), c) + result;
				} else {
					result = fullAdder(operand1.charAt(operand1.length() - i - 1),
							operand2.charAt(operand2.length() - i - 1), c).substring(1) + result;
				}

				c = fullAdder(operand1.charAt(operand1.length() - i - 1), operand2.charAt(operand2.length() - i - 1), c)
						.charAt(0);
			}
			if (c == '1') {
				isOverflow = true;
			}
			if (op1.charAt(0) == '0') {
				result = "0" + result;
			} else {
				result = "1" + result;
			}
		}
		// 符号位相反
		else {
			if (sign1.equals("0")) {
				operand1 = operand1.substring(1);
				operand2 = oneAdder(negation(operand2.substring(1))).substring(1);
				for (int i = 0; i < operand1.length(); i++) {
					if (i == operand1.length() - 1) {
						result = fullAdder(operand1.charAt(operand1.length() - i - 1),
								operand2.charAt(operand2.length() - i - 1), c).substring(1) + result;
					} else {
						result = fullAdder(operand1.charAt(operand1.length() - i - 1),
								operand2.charAt(operand2.length() - i - 1), c).substring(1) + result;
					}
					c = fullAdder(operand1.charAt(operand1.length() - i - 1),
							operand2.charAt(operand2.length() - i - 1), c).charAt(0);
				}
				if (c == '1') {
					isOverflow = false;
					result = sign1 + "0" + result;
				} else {
					result = sign2 + "0" + oneAdder(negation(result)).substring(1);
				}
			} else {
				operand2 = operand2.substring(1);
				operand1 = oneAdder(negation(operand1.substring(1))).substring(1);
				for (int i = 0; i < operand1.length(); i++) {
					if (i == operand1.length() - 1) {
						result = fullAdder(operand1.charAt(operand1.length() - i - 1),
								operand2.charAt(operand2.length() - i - 1), c).substring(1) + result;
					} else {
						result = fullAdder(operand1.charAt(operand1.length() - i - 1),
								operand2.charAt(operand2.length() - i - 1), c).substring(1) + result;
					}
					c = fullAdder(operand1.charAt(operand1.length() - i - 1),
							operand2.charAt(operand2.length() - i - 1), c).charAt(0);
				}
				if (c == '1') {
					isOverflow = false;
					result = sign2 + "0" + result;
				} else {
					result = sign1 + "0" + oneAdder(negation(result)).substring(1);
				}
			}

		}
		if (isOverflow)
			return "1" + result;
		else
			return "0" + result;
	}

	/**
	 * 浮点数加法，可调用{@link #signedAddition(String, String, int) signedAddition}
	 * 等方法实现。<br/>
	 * 例：floatAddition("00111111010100000", "00111111001000000", 8, 8, 8)
	 * 
	 * @param operand1
	 *            二进制表示的被加数
	 * @param operand2
	 *            二进制表示的加数
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @param gLength
	 *            保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相加结果，其中第1位指示是否指数上溢（溢出为1，否则为0），
	 *         其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatAddition(String operand1, String operand2, int eLength, int sLength, int gLength) {
		String result = "";
		String exponent = "";
		String significand = "";
		String sign = "";
		String sign1 = operand1.substring(0, 1);
		String sign2 = operand2.substring(0, 1);
		String exponent1 = operand1.substring(1, eLength + 1);
		String exponent2 = operand2.substring(1, eLength + 1);
		boolean isOverFlow = false;
		String significand1 = "";
		String significand2 = "";
		// 判断是否为规格化数
		boolean isNorm = true;
		if (!exponent1.contains("1")) {
			significand1 = "0" + operand1.substring(eLength + 1);
			isNorm = false;
		}
		if (!exponent2.contains("1")) {
			significand2 = "0" + operand2.substring(eLength + 1);
			isNorm = false;
		}
		if (isNorm) {
			significand1 = "1" + operand1.substring(eLength + 1);
			significand2 = "1" + operand2.substring(eLength + 1);
		}

		if (floatTrueValue(operand1, eLength, sLength) == "0") {
			return operand2;
		}
		if (floatTrueValue(operand2, eLength, sLength) == "0") {
			return operand1;
		}
		// 确定结果的符号
		if (Double.parseDouble(floatTrueValue(operand1, eLength, sLength)) >= Double
				.parseDouble(floatTrueValue(operand2, eLength, sLength))) {
			sign = sign1;
		} else {
			sign = sign2;
		}
		// 检查指数是否相同
		if (exponent1.equals(exponent2)) {
			exponent = exponent1;
		}
		while (!exponent1.equals(exponent2)) {
			if (Integer.parseInt(integerTrueValue1(exponent1)) > Integer.parseInt(integerTrueValue1(exponent2))) {
				exponent = exponent1;
				exponent2 = oneAdder(exponent2).substring(1);
				significand2 = logRightShift(significand2, 1);
				// 检查有效数是否为0
				if (!significand2.contains("1")) {
					return operand1;
				}
			}
			if (Integer.parseInt(integerTrueValue1(exponent1)) < Integer.parseInt(integerTrueValue1(exponent2))) {
				exponent = exponent2;
				exponent1 = oneAdder(exponent1).substring(1);
				significand1 = logRightShift(significand1, 1);
				// 检查有效数是否为0
				if (!significand1.contains("1")) {
					return operand2;
				}
			}
		}

		significand = signedAddition(sign1 + significand1, sign2 + significand2, (sLength / 4) * 4 + 4)
				.substring(4 + 1);

		// 有效数上溢
		if (signedAddition(sign1 + significand1, sign2 + significand2, (sLength / 4) * 4 + 4).charAt(4) == '1') {
			significand = signedAddition(sign1 + significand1, sign2 + significand2, (sLength / 4) * 4 + 4)
					.substring(4);
			// significand = logRightShift(significand, 1);
			// 指数上溢
			if (oneAdder(exponent).charAt(0) == '1') {
				if (sign.equals("1")) {
					return "+inf";
				} else {
					return "-inf";
				}
			}
			exponent = oneAdder(exponent).substring(1);
		} else {
			// 有效数为0
			if (!significand.contains("1")) {
				return "0" + floatRepresentation("0.0", eLength, sLength);
			}
			// 规格化结果
			while (significand.charAt(0) != '1') {
				significand = leftShift(significand, 1);
				// 指数下溢,反规格化
				if (integerSubtraction(exponent, "0001", eLength).charAt(0) == '1') {
					break;
				} else {
					exponent = integerSubtraction(exponent, "0001", eLength).substring(1);
				}
			}
		}

		result = sign + exponent + significand.substring(1, sLength + 1);
		if (isOverFlow) {
			result = "1" + result;
		} else {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * 浮点数减法，可调用{@link #floatAddition(String, String, int, int, int)
	 * floatAddition}方法实现。<br/>
	 * 例：floatSubtraction("00111111010100000", "00111111001000000", 8, 8, 8)
	 * 
	 * @param operand1
	 *            二进制表示的被减数
	 * @param operand2
	 *            二进制表示的减数
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @param gLength
	 *            保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相减结果，其中第1位指示是否指数上溢（溢出为1，否则为0），
	 *         其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatSubtraction(String operand1, String operand2, int eLength, int sLength, int gLength) {
		operand2 = negation(operand2.substring(0, 1)) + operand2.substring(1);
		return floatAddition(operand1, operand2, eLength, sLength, gLength);
	}

	/**
	 * 浮点数乘法，可调用{@link #integerMultiplication(String, String, int)
	 * integerMultiplication}等方法实现。<br/>
	 * 例：floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * 
	 * @param operand1
	 *            二进制表示的被乘数
	 * @param operand2
	 *            二进制表示的乘数
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），
	 *         其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatMultiplication(String operand1, String operand2, int eLength, int sLength) {
		String result = "";
		String sign = "";
		String significand = "";
		String exponent = "";
		String sign1 = operand1.substring(0, 1);
		String sign2 = operand2.substring(0, 1);
		String exponent1 = operand1.substring(1, eLength + 1);
		String exponent2 = operand2.substring(1, eLength + 1);
		String significand1 = "";
		String significand2 = "";
		// int dot1 = (floatTrueValue(operand1, eLength,
		// sLength).split("\\."))[1].length();
		// int dot2 = (floatTrueValue(operand2, eLength,
		// sLength).split("\\."))[1].length();

		String bias = integerRepresentation((int) ((Math.pow(2, eLength - 1)) - 1) + "", eLength);
		boolean isOverflow = false;
		// 判断是否为规格化数
		boolean isNorm = true;
		if (!exponent1.contains("1")) {
			significand1 = "0" + operand1.substring(eLength + 1);
			isNorm = false;
		}
		if (!exponent2.contains("1")) {
			significand2 = "0" + operand2.substring(eLength + 1);
			isNorm = false;
		}
		if (isNorm) {
			significand1 = "1" + operand1.substring(eLength + 1);
			significand2 = "1" + operand2.substring(eLength + 1);
		}
		// 乘数或被乘数为0
		if ((!operand1.contains("1")) || (!operand2.contains("1"))) {
			for (int i = 0; i < eLength + sLength + 2; i++) {
				result += "0";
			}
			return result;
		}
		// 符号位设置
		if (sign1.equals(sign2)) {
			sign = "0";
		} else {
			sign = "1";
		}
		// 指数相加减去bias
		exponent = signedAddition("0" + exponent1, "0" + exponent2, (eLength / 4) * 4 + 4).substring(6);
		exponent = signedAddition("0" + exponent, "1" + bias, (eLength / 4) * 4 + 4).substring(6);
		// 有效数相乘
		// 将有效数补成4的倍数
		int length1 = 0;
		if ((sLength + 1) % 4 != 0) {
			length1 = (((sLength + 1) / 4) * 4 + 4) - sLength - 1;
			for (int i = 0; i < length1; i++) {
				significand1 = "0" + significand1;
				significand2 = "0" + significand2;
			}
		}
		// 进行乘法时还需补上正符号位,然后再补成4的倍数
		int length2 = 4;
		significand1 = "000" + "0" + significand1;
		significand2 = "000" + "0" + significand2;
		int length = significand1.length();
		significand = integerMultiplication(significand1, significand2, length << 1).substring(1);
		significand = significand.substring(((length1 + length2) << 1));

		// 规格化
		if (significand.charAt(0) == '1') {
			significand = significand.substring(1, 1 + sLength);
			exponent = oneAdder(exponent).substring(1);
		} else if (significand.charAt(1) == '1') {
			significand = significand.substring(2, 2 + sLength);
		} else {
			String temp = significand;
			for (int i = 0; i < significand.length(); i++) {
				temp = logRightShift(temp, 1);
				if (!integerSubtraction(exponent, "0001", eLength).substring(1).contains("1")) {
					exponent = integerSubtraction(exponent, "0001", eLength).substring(1);
					significand = temp.substring(2, 2 + sLength);
					break;
				} else {
					exponent = integerSubtraction(exponent, "0001", eLength).substring(1);
					if (temp.charAt(1) == '1') {
						significand = temp.substring(2, 2 + sLength);
						break;
					}
				}
			}
		}
		result = sign + exponent + significand;
		if (isOverflow) {
			return "1" + result;
		} else {
			return "0" + result;
		}

	}

	/**
	 * 浮点数除法，可调用{@link #integerDivision(String, String, int) integerDivision}
	 * 等方法实现。<br/>
	 * 例：floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * 
	 * @param operand1
	 *            二进制表示的被除数
	 * @param operand2
	 *            二进制表示的除数
	 * @param eLength
	 *            指数的长度，取值大于等于 4
	 * @param sLength
	 *            尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），
	 *         其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatDivision(String operand1, String operand2, int eLength, int sLength) {
		String result = "";
		String sign = "";
		String significand = "";
		String exponent = "";
		String sign1 = operand1.substring(0, 1);
		String sign2 = operand2.substring(0, 1);
		String exponent1 = operand1.substring(1, eLength + 1);
		String exponent2 = operand2.substring(1, eLength + 1);
		String significand1 = "";
		String significand2 = "";
		// 符号位设置
		if (sign1.equals(sign2)) {
			sign = "0";
		} else {
			sign = "1";
		}
		// 被除数为0
		if (!operand1.contains("1")) {
			result = "0" + operand1;
			return result;
		}
		// 除数为0
		if (!operand2.contains("1")) {
			for (int i = 0; i < eLength; i++) {
				exponent += "1";
			}
			for (int i = 0; i < sLength; i++) {
				significand += "0";
			}
			result = sign + exponent + significand;
			return result;
		}
		String bias = integerRepresentation((int) ((Math.pow(2, eLength - 1)) - 1) + "", eLength);
		boolean isOverflow = false;
		// 判断是否为规格化数
		boolean isNorm = true;
		if (!exponent1.contains("1")) {
			significand1 = "0" + operand1.substring(eLength + 1);
			isNorm = false;
		}
		if (!exponent2.contains("1")) {
			significand2 = "0" + operand2.substring(eLength + 1);
			isNorm = false;
		}
		if (isNorm) {
			significand1 = "1" + operand1.substring(eLength + 1);
			significand2 = "1" + operand2.substring(eLength + 1);
		}
		if ((sLength + 1) % 4 != 0) {
			int length1 = (((sLength + 1) / 4) * 4 + 4) - sLength - 1;
			for (int i = 0; i < length1; i++) {
				significand1 = "0" + significand1;
				significand2 = "0" + significand2;
			}
		}
		// 指数相减加上bias
		if (signedAddition("0" + exponent1, "1" + exponent2, (eLength / 4) * 4 + 4).charAt(1) == '1') {
			exponent = "1" + signedAddition("0" + exponent1, "1" + exponent2, (eLength / 4) * 4 + 4).substring(6);
		} else {
			exponent = "0" + signedAddition("0" + exponent1, "1" + exponent2, (eLength / 4) * 4 + 4).substring(6);
		}
		// 检查指数是否上溢
		if (signedAddition(exponent, "0" + bias, (eLength / 4) * 4 + 4).charAt(0) == '1') {
			isOverflow = true;
		}
		exponent = signedAddition(exponent, "0" + bias, (eLength / 4) * 4 + 4).substring(6);
		// 检查指数是否下溢
		if (!exponent.contains("1")) {
			isNorm =false;
		}
		// 有效数相除
		String temp = "";
		String divisor = significand2;
		String quotient = "";
		String add = "0";
		// 保留左移出去的一位
		String extra = "0";

		for (int i = 0; i < significand1.length(); i++) {
			quotient += "0";
		}
		String remainder = significand1;
		for (int i = 0; i < divisor.length(); i++) {
			if (integerSubtraction(remainder, "0" + divisor, divisor.length()).charAt(1) == '0') {
				remainder = integerSubtraction(remainder, "0" + divisor, divisor.length()).substring(1);
				add = "1";
			} else {
				add = "0";
			}
			temp = extra + remainder + quotient + add;
			temp = leftShift(temp, 1);
			extra = temp.substring(0, 1);
			remainder = temp.substring(1, divisor.length() + 1);
			quotient = temp.substring(divisor.length() + 1, divisor.length() + divisor.length() + 1);
			add = temp.substring(divisor.length() + divisor.length() + 1);
		}
		// 商规格化
		if(isNorm){
			if (quotient.charAt(0) == '1') {
				significand = quotient.substring(1,quotient.length()-3);
			} else {
				temp = quotient;
				for (int i = 0; i < quotient.length(); i++) {
					temp = logRightShift(temp, 1);
					if (!integerSubtraction(exponent, "0001", eLength).substring(1).contains("1")) {
						exponent = integerSubtraction(exponent, "0001", eLength).substring(1);
						significand = temp.substring(1, quotient.length()-3);
						break;
					} else {
						exponent = integerSubtraction(exponent, "0001", eLength).substring(1);
						if (temp.charAt(1) == '1') {
							significand = temp.substring(1, quotient.length()-3);
							break;
						}
					}
				}
			}
		}
		else {
			significand = quotient.substring(1,quotient.length()-3);
		}

		result = sign + exponent + significand;
		if (isOverflow) {
			return "1" + result;
		} else {
			return "0" + result;
		}
	}

	// 非门
	private char notGate(char s) {
		if (s == '1') {
			return '0';
		} else {
			return '1';
		}
	}

	// 与门
	private char andGate(char s1, char s2) {
		if (s1 == '1' && s2 == '1') {
			return '1';
		} else {
			return '0';
		}
	}

	// 或门
	private char orGate(char s1, char s2) {
		if (s1 == '1' || s2 == '1') {
			return '1';
		} else {
			return '0';
		}
	}

	// 异或门
	private char xorGate(char x, char y) {
		if (x == y) {
			return '0';
		} else {
			return '1';
		}
	}

	/**
	 * 计算无符号整数的真值
	 * 
	 * @param number
	 * @return
	 */
	public String integerTrueValue1(String operand) {
		int result = 0;
		for (int i = operand.length(); i > 0; i--) {
			if (operand.substring(i - 1, i).equals("1")) {
				result += Math.pow(2, operand.length() - i);
			}
		}
		return result + "";
	}

	public static void main(String[] args) {
		ALU alu = new ALU();
		// System.out.println(alu.oneAdder("111"));
		// System.out.println(alu.signedAddition("0100000000", "0100000000",
		// 12));
		// System.out.println(alu.integerDivision("1010", "0100", 4));
		// System.out.println(alu.adder("0100", "0011", '0', 8));
		// System.out.println(alu.floatRepresentation("0.2421875",8,8));
		System.out.println(alu.floatTrueValue("01000000011100000", 8, 8));
		System.out.println(alu.floatTrueValue("00111111110000000", 8, 8));
		// System.out.println(alu.integerMultiplication("0000000110000000",
		// "0000000110000000", 32));
		// System.out.println(alu.integerMultiplication("0010", "0010", 4));
		// System.out.println(alu.floatTrueValue("00111111111100000", 8,8));
		// System.out.println(alu.integerAddition("01111110", "01111101", 8));
		System.out.println(alu.floatMultiplication("01000000011100000", "00111111110000000", 8, 8).substring(1));
		System.out.println(alu
				.floatTrueValue(alu.floatMultiplication("01000000011100000", "00111111110000000", 8, 8).substring(1), 8, 8));
		System.out.println(alu.floatDivision("01000000011100000", "00111111110000000", 8, 8).substring(1));
		System.out.println(alu
				.floatTrueValue(alu.floatDivision("01000000011100000", "00111111110000000", 8, 8).substring(1), 8, 8));
		// System.out.println(alu.integerMultiplication("0111000000",
		// "0100000000", 32));
		// System.out.println(alu.floatTrueValue("00111111100100000", 8, 8));
		// System.out.println(alu.integerSubtraction("0111000000", "0100000000",
		// 12));
	}

}
