package org.metaborg.core.transform;

import java.util.Collection;

import org.metaborg.core.action.ITransformGoal;
import org.metaborg.core.action.TransformActionContribution;
import org.metaborg.core.analysis.IAnalyzeUnit;
import org.metaborg.core.context.IContext;
import org.metaborg.core.syntax.IParseUnit;

/**
 * Interface for transformations on parsed or analyzed files.
 * 
 * @param <P>
 *            Type of parse units.
 * @param <A>
 *            Type of analyze units.
 * @param <TP>
 *            Type of transform units with parse units as input.
 * @param <TA>
 *            Type of transform units with analyze units as input.
 */
public interface ITransformService<P extends IParseUnit, A extends IAnalyzeUnit, TP extends ITransformUnit<P>, TA extends ITransformUnit<A>> {
    /**
     * Checks if transform actions are available for given context and goal.
     * 
     * @param context
     *            Context to check transform actions from.
     * @param goal
     *            Goal to check transform actions for.
     * @return True if transform actions are available, false if not.
     */
    boolean available(IContext context, ITransformGoal goal);

    /**
     * Checks if analysis is required before transformation, for given context and goal.
     * 
     * @param context
     *            Context to check from.
     * @param goal
     *            Goal to check for.
     * @return True if analysis is required, false if not.
     */
    boolean requiresAnalysis(IContext context, ITransformGoal goal);


    /**
     * Transforms parse input in a context with given goal.
     * 
     * @param input
     *            Parsed input to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param goal
     *            Transform goal to execute.
     * @return Transformation result.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    Collection<TP> transform(P input, IContext context, ITransformGoal goal) throws TransformException;

    /**
     * Transforms parse input in a context with given action.
     * 
     * @param input
     *            Parsed input to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param action
     *            Transform action to execute.
     * @return Transformation result.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     * @throws TransformException
     *             When transformation action requires analysis.
     */
    TP transform(P input, IContext context, TransformActionContribution action) throws TransformException;

    /**
     * Transforms analyzed input in a context with given goal.
     * 
     * @param input
     *            Analyzed input to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param goal
     *            Transform goal to execute.
     * @return Transformation result.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    Collection<TA> transform(A input, IContext context, ITransformGoal goal) throws TransformException;

    /**
     * Transforms analyzed input in a context with given action.
     * 
     * @param input
     *            Analyzed input to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param action
     *            Transform action to execute.
     * @return Transformation result.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    TA transform(A input, IContext context, TransformActionContribution action) throws TransformException;


    /**
     * Transforms parse inputs in a context with given goal.
     * 
     * @param inputs
     *            Parsed inputs to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param goal
     *            Transform goal to execute.
     * @return Transformation results.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    Collection<TP> transformAllParsed(Iterable<P> inputs, IContext context, ITransformGoal goal)
        throws TransformException;

    /**
     * Transforms parse input in a context with given action.
     * 
     * @param inputs
     *            Parsed inputs to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param action
     *            Transform action to execute.
     * @return Transformation results.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     * @throws TransformException
     *             When transformation action requires analysis.
     */
    Collection<TP> transformAllParsed(Iterable<P> inputs, IContext context, TransformActionContribution action)
        throws TransformException;

    /**
     * Transforms analyzed inputs in a context with given goal.
     * 
     * @param inputs
     *            Analyzed inputs to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param goal
     *            Transform goal to execute.
     * @return Transformation results.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    Collection<TA> transformAllAnalyzed(Iterable<A> inputs, IContext context, ITransformGoal goal)
        throws TransformException;

    /**
     * Transforms analyzed inputs in a context with given action.
     * 
     * @param input
     *            Analyzed inputs to transform.
     * @param context
     *            Context in which to apply transformation.
     * @param action
     *            Transform action to execute.
     * @return Transformation results.
     * @throws TransformException
     *             When the transformation fails unexpectedly.
     */
    Collection<TA> transformAllAnalyzed(Iterable<A> inputs, IContext context, TransformActionContribution action)
        throws TransformException;
}
