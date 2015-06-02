
package futuretask;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import callable.SpecializedCallable;

public class SpecializedFutureTask<V> extends FutureTask<V>
	{
	
	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/
	public SpecializedFutureTask(Callable<V> callable)
		{
		super(callable);
		this.callable = (SpecializedCallable)callable;
		}
	
	/*------------------------------------------------------------------*\
	|*							Methods Public							*|
	\*------------------------------------------------------------------*/
	
	public void modifyCallable(boolean showComputations)
		{
		this.callable.setShowComputations(showComputations);
		}
	
	/*------------------------------------------------------------------*\
	|*							Methods Private							*|
	\*------------------------------------------------------------------*/
	
	/*------------------------------------------------------------------*\
	|*							Attributes Private						*|
	\*------------------------------------------------------------------*/
	
	// Inputs
	private SpecializedCallable callable;
	
	}
