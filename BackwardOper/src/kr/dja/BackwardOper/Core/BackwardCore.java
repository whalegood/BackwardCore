package kr.dja.BackwardOper.Core;
/*
* 스택을 활용한 후위표기법 계산기.
* 1661030 엄선용
* 
* 메인 메서드, 주요 연산 메서드 부분.
*/
/*
* 5월 2일 수정
* 연산 과정 표시
*/
import java.util.Scanner;
import kr.dja.BackwardOper.Operator.ABS_CalMember;
import kr.dja.BackwardOper.Operator.ABS_Calculer;
import kr.dja.BackwardOper.Operator.NumOperand;
import kr.dja.BackwardOper.Stack.Stack;
import java.math.BigInteger;
public class BackwardCore
{
public static void main(String[] args) throws ClassNotFoundException
{
System.out.println("후위연산 계산기. 나가려면 exit를 입력하세요.");
Scanner scanner = new Scanner(System.in);
String input;
while(true)
{
Stack<ABS_CalMember> value = new Stack<ABS_CalMember>(3);
System.out.print("\n수식을 입력하세요>>");
input = scanner.nextLine();
if(input.equals("exit")) break;
try { postfixExp(input, value); }// 후위 표기 스택 가져오기.
catch(Exception e)
{
e.printStackTrace();
System.out.println("후위 표기 결과를 가져오던 중 오류가 발생하였습니다.");
continue;
}
System.out.print("후위 표기 식입니다:");
for(int i = 0; i < value.size(); i++)
{// 후위 표기 스택에서 하나씩 출력.
System.out.print(" " + value.getMemberAt(i));
}
try { System.out.println("\n연산 결과입니다: " + getValue(value)); }
catch(Exception e)
{ System.out.println("\n연산 과정중 오류가 발생하였습니다."); }
}
System.out.println("프로그램을 종료합니다.");
scanner.close();
}
private static Stack<ABS_CalMember> postfixExp(String str, Stack<ABS_CalMember> calStack)
{// 5월 2일 연산 과정 표시 수정.
Stack<ABS_CalMember> tempStack = new Stack<ABS_CalMember>(calStack.getSlotSize());// 연산을 위한 임시 스택.
int taskInteger = 0;// 추출중인 숫자.
boolean numberTask = false;// 숫자 추출 작업 상태를 표시.
System.out.printf("%-2s | %-7s | %-30s | %-50s\n", "In", "TaskInt", "Stack", "Out");
for(int i = 0; i < str.length(); i++)
{// 한 문자씩 읽기.
char taskChar = str.charAt(i);
if(taskChar >= '0' && taskChar <= '9')
{
if(!numberTask) taskInteger = 0;
taskInteger = (taskInteger * 10) + (taskChar - '0');
numberTask = true;
}
else if(ABS_CalMember.isExist(String.valueOf(taskChar)))
{// 입력한 문자가 연산 토큰일 경우.
ABS_CalMember operator = ABS_CalMember.getInstance(String.valueOf(taskChar));
if(numberTask)
{
calStack.pushBack(new NumOperand(taskInteger));
numberTask = false;
}
if(operator.toString().equals("("))
{// (를 만나면 임시 스택에 푸시한다.
tempStack.pushBack(operator);
}
else if(operator.toString().equals(")"))
{// )를 만나면 임시 스택에서 ( 가 나올 때까지 팝하고, (는 임시 스택에서 팝하여 버린다.
while(tempStack.size() >= 0)
{
if(tempStack.getBack().toString().equals("("))
{
tempStack.popBack();
break;
}
calStack.pushBack(tempStack.getBack());
tempStack.popBack();
}
}
else if(operator instanceof ABS_Calculer)
{// 괄호가 아닐경우.
ABS_Calculer calOper = (ABS_Calculer)operator;
while(true)
{// 연산자를 만나면 임시 스택에서 그 연산자보다 낮은 우선순위의 연산자를 만날 때까지 팝하여 
// 후위 표기 스택에 저장한 뒤에 자신을 푸시한다.
if(tempStack.size() == 0)
{// 임시 스택이 비었을 경우 빠져나감.
tempStack.pushBack(operator);
break;
}
ABS_Calculer calculer = tempStack.getBack() instanceof
ABS_Calculer ?
(ABS_Calculer)tempStack.getBack() : null;
if(calculer == null || calculer.getPriority() <
calOper.getPriority())
{// 낮은 우선순위의 연산자를 만났거나, 괄호를 만났을경우 빠져나감.
tempStack.pushBack(calOper);
break;
}
calStack.pushBack(tempStack.getBack());
tempStack.popBack();
}
}
}
System.out.printf("%-2s | %-7s | %-30s | %-50s\n", taskChar, taskInteger,
tempStack.toString(), calStack.toString());
}
if(numberTask) calStack.pushBack(new NumOperand(taskInteger));
while(tempStack.size() > 0)
{// 마지막 남은 연산자들을 푸시.
calStack.pushBack(tempStack.getBack());
tempStack.popBack();
}
return calStack;
}
private static NumOperand getValue(Stack<ABS_CalMember> postfixStack)
{// 후위 표기 스택을 바탕으로 값 가져오기.
Stack<NumOperand> tempNumStack = new Stack<NumOperand>(postfixStack.getSlotSize());
for(int i = 0; i < postfixStack.size(); i++)
{
ABS_CalMember taskMember = postfixStack.getMemberAt(i);
NumOperand x, y, number;
ABS_Calculer calculer;
if(taskMember instanceof NumOperand)
{// 읽은 멤버가 피연산자라면.
number = (NumOperand)taskMember;
tempNumStack.pushBack(number);
}
else if(taskMember instanceof ABS_Calculer)
{// 읽은 멤버가 연산자라면.
calculer = (ABS_Calculer)taskMember;
// 임시 스택에서 두 피 연산자를 꺼낸다음 연산한 다음 결과를 다시 푸시.
x = tempNumStack.getBack(); tempNumStack.popBack();
y = tempNumStack.getBack(); tempNumStack.popBack();
tempNumStack.pushBack(calculer.task(y, x));
}
}
if(tempNumStack.getMemberAt(0) == null) throw new NullPointerException();
return tempNumStack.getMemberAt(0);// 임시 스택에 마지막 남은 숫자가 결과.
}
}