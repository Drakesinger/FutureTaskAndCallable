
package Uses;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import futuretask.SpecializedFutureTask;
import callable.SpecializedCallable;

import com.bilat.tools.io.console.Clavier;

public class UseCallable
	{
	
	/**
	 * Find Pi, using the Integral of 
	 * 			1/(1+x*x)
	 * between 0 and 1;
	 */
	public static void main(String[] args)
		{
		System.out.println("Méthode parallèle précise avec Callable pour trouver Pi :");
		piParallelCallable();
		//		
		//		System.out.println("\nMéthode séquentielle précise pour trouver Pi :");
		//		piSequentielPrecis();
		//		
		//		System.out.println("\nMéthode séquentielle pour trouver Pi :");
		//		piSequentiel();
		}
	
	/**
	 * Execution Time : 190 sec = 3 minutes ( which is about 2 times shorter than the same algorithm used in sequential)
	 */
	private static void piParallelCallable()
		{
		int precision = 22;
		BigDecimal somme_totale = new BigDecimal(0);
		
		long start = System.currentTimeMillis();
		
		// Math Context set up to a certain precision.
		MathContext mathContext = new MathContext(precision);
		
		// Get ExecutorService from Executors utility class, thread pool size is equal to available processors on the computer
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		// Create a list to hold the FutureTesk objects associated with Callable.
		List<FutureTask<BigDecimal>> listOfFutureTasks = new ArrayList<FutureTask<BigDecimal>>();
		
		// Create MyCallable instance
		Callable<BigDecimal> callable;
		
		BigDecimal b = new BigDecimal(1);
		BigDecimal a = new BigDecimal(0);
		BigDecimal n = new BigDecimal(288675135);
		
		// Computation variables		
		BigDecimal h = (b.subtract(a)).divide(n, mathContext); // h = (b-a)/n
		// step = n/(4), precision of 8, rounded up
		// divide by 4 (due to nr of processors)
		BigDecimal step = n.divide(new BigDecimal(4), new MathContext(8, RoundingMode.CEILING));
		System.out.println(step);
		
		// From = 0.5
		BigDecimal from = new BigDecimal(0.5);
		BigDecimal to = step;
		
		/*
		 * step = to = n/4 (rounded to CEIL), from = 0.5, n = 288675135 (for sufficient precision)
		 * 
		 * Algorithm - distribute computations to 4 Callable
		 * ----------------------
		 * In our case we compute an integral with the rectangles algorithm
		 * h = base width of each rectangle
		 * from/n = middle of 1st rectangle's base
		 * to/n =  middle of last rectangle's base
		 * While (to <= n)
		 * 		create a new Callable which computes, in our case, part of the Integral [ 1/(1+(x*x)) ] from variable 'a' to variable 'b'
		 * 		  the part is defined by from, to, and h variables
		 * 		  mathContext variable is about computation precision
		 * 		submit the new task to the Executor
		 * 
		 * in our case, sum of the 4 part of the Integral = Pi/4
		 * ----------------------
		 */
		while(to.compareTo(n) <= 0)
			{
			callable = new SpecializedCallable(from, to, h, mathContext);
			FutureTask<BigDecimal> futureTask = new SpecializedFutureTask<BigDecimal>(callable);
			executor.submit(futureTask);
			System.out.println("Submited task.");
			// Add the Future task to the list.
			listOfFutureTasks.add(futureTask);
			
			from = from.add(step);
			to = to.add(step);
			}
		
		// Trick:
		// another callable because we increment 'to' by 'step' value, which was rounded to CEIL
		// and therefore, 'to' will be greater than 'n' at the fourth passage in the while loop
		// and we need to consider the fourth callable from 'from' to 'n', (not from 'from' to
		// 'n+something')
		callable = new SpecializedCallable(from, n, h, mathContext);
		FutureTask<BigDecimal> futureT = new SpecializedFutureTask<BigDecimal>(callable);
		executor.submit(futureT);
		listOfFutureTasks.add(futureT);
		
		// Get the results, give the user a choice to cancel the tasks.
		for(FutureTask<BigDecimal> futureTask:listOfFutureTasks)
			{
			try
				{
				String stringRepresentation = futureTask.toString();
				int indexOfFutureTask = listOfFutureTasks.indexOf(futureTask);
				String isDone = stringRepresentation + " @index:" + indexOfFutureTask + " task? Is done?" + futureTask.isDone();
				System.out.println(isDone);
				
				String cancelQuerry = "Do you want to cancel this " + stringRepresentation + " @index:" + indexOfFutureTask + " task? (1/0)";
				String modifyQuerry = "Do you want to show it's computations? (1/0)";
				
				while(!futureTask.isDone())
					{
					System.out.println(cancelQuerry);
					int input = Clavier.lireInt();
					
					switch(input)
						{
						case 1:
							futureTask.cancel(true);
							break;
						case 0:
							break;
						}
					
					System.out.println(modifyQuerry);
					input = Clavier.lireInt();
					
					switch(input)
						{
						case 1:
							((SpecializedFutureTask<BigDecimal>)futureTask).modifyCallable(true);
							break;
						case 0:
							((SpecializedFutureTask<BigDecimal>)futureTask).modifyCallable(false);
							break;
						}
					}
				
				// Future.get() waits for task to get completed
				somme_totale = somme_totale.add(futureTask.get());
				}
			catch (InterruptedException | ExecutionException e)
				{
				e.printStackTrace();
				}
			catch (CancellationException ce)
				{
				System.out.println("The task was canceled.");
				}
			}
		
		// Shut down the executor service now
		executor.shutdown();
		
		somme_totale = somme_totale.multiply(new BigDecimal(4));
		
		long stop = System.currentTimeMillis();
		
		BigDecimal pi_exact = new BigDecimal("3.141592653589793238462643383279");
		
		// Print the result
		System.out.println("Temps :" + (stop - start) + "[ms]");
		System.out.println("Pi calc. : " + somme_totale);
		System.out.println("Pi exact : " + pi_exact);
		System.out.println("Différence avec valeur exacte : " + pi_exact.subtract(somme_totale).abs());
		
		}
	
	// ~2seconds, due to lack of precision
	private static void piSequentiel()
		{
		
		long start = System.currentTimeMillis();
		
		double b = 1;
		double a = 0;
		double n = 288675135;
		
		// Variables de calcul
		double result = 0.0;
		double h = (b - a) / n;
		
		for(double i = 0.5; i < n; i += 1.0)
			{
			double factor = h * i; // 2.0*(i*h) - h / 2.0
			result += h * (1.0 / (1.0 + (factor * factor)));
			}
		result = 4.0 * result;
		
		long stop = System.currentTimeMillis();
		
		// Impression du résultat
		System.out.println("Pi est : " + result);
		System.out.println("Temps :" + (stop - start) + "[ms]");
		System.out.println("Différence avec valeur exacte : " + Math.abs(result - 3.141592653589793238462643383279));
		}
	
	/**
	 * Temps d'exécution : 373 sec = env 6 minutes
	 */
	@SuppressWarnings("unused")
	private static void piSequentielPrecis()
		{
		
		long start = System.currentTimeMillis();
		
		MathContext mathContext = new MathContext(22);
		
		BigDecimal b = new BigDecimal(1);
		BigDecimal a = new BigDecimal(0);
		
		// Variables de calcul
		BigDecimal n = new BigDecimal(288675135);
		BigDecimal h = (b.subtract(a)).divide(n, mathContext);
		BigDecimal somme_totale = new BigDecimal(0);
		BigDecimal one = new BigDecimal(1);
		
		for(BigDecimal i = new BigDecimal(0.5); i.compareTo(n) < 0; i = i.add(one))
			{
			BigDecimal factor = h.multiply(i); // 2.0*(i*h) - h / 2.0
			somme_totale = somme_totale.add(h.multiply(one.divide(one.add(factor.multiply(factor)), mathContext)));
			}
		
		somme_totale = somme_totale.multiply(new BigDecimal(4));
		
		long stop = System.currentTimeMillis();
		
		BigDecimal pi_exact = new BigDecimal("3.141592653589793238462643383279");
		
		// Impression du résultat
		System.out.println("Temps :" + (stop - start) + "[ms]");
		System.out.println("Pi calc. : " + somme_totale);
		System.out.println("Pi exact : " + pi_exact);
		System.out.println("Différence avec valeur exacte : " + pi_exact.subtract(somme_totale).abs());
		}
	
	}
