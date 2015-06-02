
package futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import callable.CallableExemple;

public class SpecializedFutureTask<V> extends FutureTask<V>
	{
	
	public SpecializedFutureTask(Callable<V> callable)
		{
		super(callable);
		this.callable = (CallableExemple)callable;
		}
	
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/
	
	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/
	
	public void modifyCallable(boolean showComputations)
		{
		this.callable.setShowComputations(showComputations);
		}
	
	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/
	
	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/
	
	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/
	
	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/
	private CallableExemple callable;
	
	}
