package org.andresoviedo.util.tasks;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DependantTaskExecutorTest {

	@Test
	public void test() throws Exception {
		DependantTasksExecutor executor = new DependantTasksExecutor(10);
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 11111");
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 11111");
				return 1;
			}
		}, "1", null);
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 22222");
				System.out.println("<< 22222 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 22222");
				return 2;
			}
		}, "2", new String[] { "1" });
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 33333");
				System.out.println("<< 33333 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 33333");
				if (true) {
					throw new IllegalArgumentException();
				}
				return 3;
			}
		}, "3", new String[] { "1" });
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 44444");
				System.out.println("<< 44444 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 44444");
				return 4;
			}
		}, "4", new String[] { "1", "3" });

		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 55555");
				System.out.println("<< 55555 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 55555");
				return 5;
			}
		}, "5", new String[] { "4" });

		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 66666");
				System.out.println("<< 66666 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 66666");
				return 6;
			}
		}, "6", new String[] { "5", "1", "2", "3", "4" });

		executor.shutdown();
		executor.awaitTermination(40, TimeUnit.SECONDS);
	}
}
