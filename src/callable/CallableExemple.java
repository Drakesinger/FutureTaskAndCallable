
package callable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.Callable;

/*
 * Compute a part of the Integral of :
 * 
 *  			h*(1/1+x2) 			
 *  
 *  ( x2 = pow(x,2) )
 *  
 *  in order to get Pi in our case
 *  
 */
public class CallableExemple implements Callable<BigDecimal>
	{
	
	public CallableExemple(BigDecimal from, BigDecimal to, BigDecimal h, MathContext mathContext)
		{
		//		System.out.println("From :" + from + " / to : " + to + " / h : " + h);
		this.from = from;
		this.to = to;
		this.h = h;
		this.mathContext = mathContext;
		
		this.showComputations = false;
		this.result = new BigDecimal(0);
		}
	
	/*
	 * Public Methods 
	 */
	
	@Override
	public BigDecimal call() throws Exception
		{
		BigDecimal one = new BigDecimal(1);
		for(BigDecimal i = from; i.compareTo(to) < 0; i = i.add(one))
			{
			BigDecimal factor = h.multiply(i); //2.0*(i*h) - h / 2.0
			
			if (showComputations)
				{
				System.out.println("Computing: " + this.toString() + " result: " + result);
				}
			result = result.add(h.multiply(one.divide(one.add(factor.multiply(factor)), mathContext)));
			}
		
		// return the partial sum of this thread
		return result;
		}
	
	/*
	 * Getters / Setters 
	 */
	
	public boolean isShowComputations()
		{
		return this.showComputations;
		}
	
	public void setShowComputations(boolean showComputations)
		{
		this.showComputations = showComputations;
		}
	
	/*
	 * Attributes
	 */
	// Inputs
	private BigDecimal from;
	
	private BigDecimal to;
	private BigDecimal h;
	private MathContext mathContext;
	
	// Tools
	private boolean showComputations;
	
	// Output
	private BigDecimal result;
	
	}
