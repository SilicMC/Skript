/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

@Name("Break Block")
@Description({"Breaks the block and spawns items as if a player had mined it",
	"\nYou can add a tool, which will spawn items based on how that tool would break the block ",
	"(ie: When using a hand to break stone, it drops nothing, whereas with a pickaxe it drops cobblestone)"})
@Examples({"make all creepers pathfind towards player"})
@Since("INSERT VERSION")
public class EffPathfind extends Effect {

	static {
		if (Skript.methodExists(Mob.class, "getPathfinder"))
			Skript.registerEffect(EffPathfind.class,
				"make %livingentities% (pathfind|move) to[wards] %livingentity/location% [at speed %-number%]",
				"make %livingentities% stop (pathfinding|moving)");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<LivingEntity> entities;

	@Nullable
	private Expression<?> target;

	@Nullable
	private Expression<Number> speed;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		entities = (Expression<LivingEntity>) exprs[0];
		target = matchedPattern == 0 ? exprs[1] : null;
		speed = matchedPattern == 0 ? (Expression<Number>) exprs[2] : null;
		return true;
	}

	@Override
	protected void execute(Event e) {
		Object target = this.target != null ? this.target.getSingle(e) : null;
		Number speed = this.speed != null ? this.speed.getSingle(e) : null;
		for (LivingEntity entity : entities.getArray(e)) {
			if (entity instanceof Mob) {
				if (target instanceof LivingEntity) {
					((Mob) entity).getPathfinder().moveTo((LivingEntity) target, speed != null ? speed.intValue() : 1);
				} else if (target instanceof Location) {
					((Mob) entity).getPathfinder().moveTo((Location) target, speed != null ? speed.intValue() : 1);
				} else if (this.target == null) {
					((Mob) entity).getPathfinder().stopPathfinding();
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		if (target == null) {
			return "make " + entities.toString(e, debug) + " stop pathfinding";
		}

		String repr = "make " + entities.toString(e, debug) + " pathfind towards " + target.toString(e, debug);
		if (speed != null) {
			repr += " at speed " + speed.toString(e, debug);
		}
		return repr;
	}

}
