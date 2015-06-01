
package Uses;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import callable.CallableExemple;

public class UseCallable
	{
	
	/**
	 * Trouver Pi en calculant l'intégrale de 1/(1+x*x) entre 0 et 1;
	 */
	public static void main(String[] args)
		{
		System.out.println("Méthode parallèle précise avec Callable pour trouver Pi :");
		piParallelCallable();
		
		System.out.println("\nMéthode séquentielle précise pour trouver Pi :");
		// piSequentielPrecis();
		
		System.out.println("\nMéthode séquentielle pour trouver Pi :");
		piSequentiel();
		
		}
	
	/**
	 * Temps d'exécution : 190 sec = environ 3 minutes ( ce qui est environ 2x plus court que minutes de mieux que le même algo en séquentiel)
	 */
	private static void piParallelCallable()
		{
		BigDecimal somme_totale = new BigDecimal(0);
		
		long start = System.currentTimeMillis();
		
		MathContext mathContext = new MathContext(22);
		
		// Get ExecutorService from Executors utility class, thread pool size is
		// equal to available processors on the computer
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// create a list to hold the Future object associated with Callable
		List<Future<BigDecimal>> list = new ArrayList<Future<BigDecimal>>();
		// Create MyCallable instance
		Callable<BigDecimal> callable;
		
		BigDecimal b = new BigDecimal(1);
		BigDecimal a = new BigDecimal(0);
		BigDecimal n = new BigDecimal(288675135);
		// BigDecimal n = new BigDecimal(8675135);
		
		// Variables de calcul
		BigDecimal h = (b.subtract(a)).divide(n, mathContext);
		
		BigDecimal step = n.divide(new BigDecimal(4), new MathContext(8, RoundingMode.CEILING));
		System.out.println(step);
		
		BigDecimal from = new BigDecimal(0.5);
		BigDecimal to = step;
		
		while(to.compareTo(n) <= 0)
			{
			
			callable = new CallableExemple(from, to, h, mathContext);
			// submit Callable tasks to be executed by thread pool
			Future<BigDecimal> future = executor.submit(callable);
			
			///////////////////////////////////////////////////////////////////////////////////////////////////
			// Same thing with FutureTask /////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////
				{
			FutureTask<BigDecimal> futureTask = new FutureTask<BigDecimal>(callable);
			executor.submit(futureTask);
			// TODO
			//add the Future task to the list.
				}
			
			// add Future to the list, we can get return value using Future
			list.add(future);
			from = from.add(step);
			to = to.add(step);
			}
		
		
		callable = new CallableExemple(from, n, h, mathContext);
		Future<BigDecimal> future = executor.submit(callable);
		list.add(future);
		
		for(Future<BigDecimal> fut:list)
			{
			try
				{
				// Future.get() waits for task to get completed
				somme_totale = somme_totale.add(fut.get());
				}
			catch (InterruptedException | ExecutionException e)
				{
				e.printStackTrace();
				}
			}
		// shut down the executor service now
		executor.shutdown();
		
		somme_totale = somme_totale.multiply(new BigDecimal(4));
		
		long stop = System.currentTimeMillis();
		
		BigDecimal pi_exact = new BigDecimal("3.141592653589793238462643383279");
		
		// Impression du résultat
		System.out.println("Temps :" + (stop - start) + "[ms]");
		System.out.println("Pi calc. : " + somme_totale);
		System.out.println("Pi exact : " + pi_exact);
		System.out.println("Différence avec valeur exacte : " + pi_exact.subtract(somme_totale).abs());
		
		}
	
	// environ 2 secondes
	private static void piSequentiel()
		{
		
		long start = System.currentTimeMillis();
		
		double b = 1;
		double a = 0;
		double n = 288675135;
		// double n = 87093;
		
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
	
	/*
	 * Attributs statics
	 */
	
	// private static final int X = 1;
	// private static final int Y = 800000;
	
	}
