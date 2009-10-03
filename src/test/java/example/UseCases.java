package example;

import java.lang.reflect.InvocationTargetException;

import org.jpef.parallel.MyService;
import org.jpef.parallel.MyServiceImpl;
import org.jpef.parallel.Parallel;

/**
 * Common use cases for JPEF.
 * 
 * @author Gergely Kiss
 */
public class UseCases {
	public static void main(String[] args) {
		new UseCase1();
	}
}

/*
 * Parallelizing an existing bean.
 */
class UseCase1 {
	MyService service = new MyServiceImpl();

	public UseCase1() {

		// Invoking a parallel proxy
		MyService proxy = Parallel.proxy(service, MyService.class);
		int res = proxy.sum(new int[] { 1, 2, 3, 4, 5 });
		System.err.println("Sum is: " + res);

		// Invoking a single method
		try {
			int res2 = Parallel.invoke(service, "sum", int.class, new int[] { 1, 2, 3, 4, 5 });
			System.err.println("Sum is: " + res2);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}