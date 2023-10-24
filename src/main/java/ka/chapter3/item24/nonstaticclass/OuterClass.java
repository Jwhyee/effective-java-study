package ka.chapter3.item24.nonstaticclass;

public class OuterClass {
    private int outerData;

    public void outerMethod(int num) {
        NonStaticInnerClass inner = new NonStaticInnerClass();
        this.outerData = num;
        inner.innerMethod();
    }

    public class NonStaticInnerClass {
        public void innerMethod() {
            System.out.println("Inner method called");
            // 암묵적 연결을 이용한 바깥 클래스 멤버에 접근
            int data = OuterClass.this.outerData;
            System.out.println("Accessing outer class data: " + data);
        }
    }
}