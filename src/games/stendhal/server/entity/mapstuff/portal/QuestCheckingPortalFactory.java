/*
 * @(#) src/games/stendhal/server/entity/portal/QuestCheckingPortalFactory.java
 *
 * $Id: QuestCheckingPortalFactory.java,v 1.4 2008/07/12 14:43:59 astridemma Exp $
 */

package games.stendhal.server.entity.mapstuff.portal;

//
//

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;

/**
 * A factory for <code>QuestCheckingPortal</code> objects.
 */
public class QuestCheckingPortalFactory extends AccessCheckingPortalFactory {

	//
	// QuestCheckingPortalFactory
	//

	/**
	 * Extract the quest name from a context.
	 * 
	 * @param ctx
	 *            The configuration context.
	 * @return The quest name.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected String getQuest(final ConfigurableFactoryContext ctx) {
		return ctx.getRequiredString("quest");
	}

	//
	// AccessCheckingPortalFactory
	//

	/**
	 * Create a quest checking portal.
	 * 
	 * @param ctx
	 *            Configuration context.
	 * 
	 * @return A Portal.
	 * 
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 * 
	 * @see LevelCheckingPortal
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		return new QuestCheckingPortal(getQuest(ctx));
	}
}
