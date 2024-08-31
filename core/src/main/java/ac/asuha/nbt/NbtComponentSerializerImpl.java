package ac.asuha.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import ac.asuha.utils.Check;
import org.jetbrains.annotations.NotNull;

final class NbtComponentSerializerImpl implements NbtComponentSerializer {
    static final NbtComponentSerializer INSTANCE = new NbtComponentSerializerImpl();

    NbtComponentSerializerImpl() {
    }

    public @NotNull Component deserialize(@NotNull BinaryTag input) {
        return this.deserializeAnyComponent(input);
    }

    public @NotNull BinaryTag serialize(@NotNull Component component) {
        return this.serializeComponent(component);
    }

    private @NotNull Component deserializeAnyComponent(@NotNull BinaryTag nbt) {
        Object var10000;
        switch (nbt) {
            case CompoundBinaryTag compound:
                var10000 = this.deserializeComponent(compound);
                break;
            case StringBinaryTag string:
                var10000 = Component.text(string.value());
                break;
            case ListBinaryTag list:
                TextComponent.Builder builder = Component.text();
                Iterator var8 = list.iterator();

                while(var8.hasNext()) {
                    BinaryTag element = (BinaryTag)var8.next();
                    builder.append(this.deserializeAnyComponent(element));
                }

                var10000 = (TextComponent)builder.build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown NBT type: " + nbt.getClass().getName());
        }

        return (Component)var10000;
    }

    private @NotNull Component deserializeComponent(@NotNull CompoundBinaryTag compound) {
        BinaryTag type = compound.get("type");
        Object builder;
        if (type instanceof StringBinaryTag) {
            StringBinaryTag sType = (StringBinaryTag)type;
            ComponentBuilder var10000;
            switch (sType.value()) {
                case "text" -> var10000 = this.deserializeTextComponent(compound);
                case "translatable" -> var10000 = this.deserializeTranslatableComponent(compound);
                case "score" -> var10000 = this.deserializeScoreComponent(compound);
                case "selector" -> var10000 = this.deserializeSelectorComponent(compound);
                case "keybind" -> var10000 = this.deserializeKeybindComponent(compound);
                case "nbt" -> var10000 = this.deserializeNbtComponent(compound);
                default -> throw new UnsupportedOperationException("Unknown component type: " + String.valueOf(type));
            }

            builder = var10000;
        } else {
            Set<String> keys = compound.keySet();
            if (keys.isEmpty()) {
                return Component.empty();
            }

            if (keys.contains("text")) {
                builder = this.deserializeTextComponent(compound);
            } else if (keys.contains("translate")) {
                builder = this.deserializeTranslatableComponent(compound);
            } else if (keys.contains("score")) {
                builder = this.deserializeScoreComponent(compound);
            } else if (keys.contains("selector")) {
                builder = this.deserializeSelectorComponent(compound);
            } else if (keys.contains("keybind")) {
                builder = this.deserializeKeybindComponent(compound);
            } else if (keys.contains("nbt")) {
                builder = this.deserializeNbtComponent(compound);
            } else {
                if (!keys.contains("")) {
                    throw new UnsupportedOperationException("Unable to infer component type");
                }

                builder = Component.text().content(compound.getString(""));
            }
        }

        ListBinaryTag extra = compound.getList("extra");
        if (extra.size() > 0) {
            ArrayList<ComponentLike> list = new ArrayList();
            Iterator var11 = extra.iterator();

            while(var11.hasNext()) {
                BinaryTag child = (BinaryTag)var11.next();
                list.add(this.deserializeAnyComponent(child));
            }

            ((ComponentBuilder)builder).append(list);
        }

        ((ComponentBuilder)builder).style(this.deserializeStyle(compound));
        return ((ComponentBuilder)builder).build();
    }

    public @NotNull Style deserializeStyle(@NotNull BinaryTag tag) {
        if (tag instanceof CompoundBinaryTag compound) {
            Style.Builder style = Style.style();
            String color = compound.getString("color");
            if (!color.isEmpty()) {
                TextColor hexColor = TextColor.fromHexString(color);
                if (hexColor != null) {
                    style.color(hexColor);
                } else {
                    NamedTextColor namedColor = (NamedTextColor)NamedTextColor.NAMES.value(color);
                    if (namedColor == null) {
                        throw new UnsupportedOperationException("Unknown color: " + color);
                    }

                    style.color(namedColor);
                }
            }

            String font = compound.getString("font");
            if (!font.isEmpty()) {
                style.font(Key.key(font));
            }

            BinaryTag bold = compound.get("bold");
            if (bold instanceof ByteBinaryTag b) {
                style.decoration(TextDecoration.BOLD, b.value() == 1 ? State.TRUE : State.FALSE);
            }

            BinaryTag italic = compound.get("italic");
            if (italic instanceof ByteBinaryTag b) {
                style.decoration(TextDecoration.ITALIC, b.value() == 1 ? State.TRUE : State.FALSE);
            }

            BinaryTag underlined = compound.get("underlined");
            if (underlined instanceof ByteBinaryTag b) {
                style.decoration(TextDecoration.UNDERLINED, b.value() == 1 ? State.TRUE : State.FALSE);
            }

            BinaryTag strikethrough = compound.get("strikethrough");
            if (strikethrough instanceof ByteBinaryTag b) {
                style.decoration(TextDecoration.STRIKETHROUGH, b.value() == 1 ? State.TRUE : State.FALSE);
            }

            BinaryTag obfuscated = compound.get("obfuscated");
            if (obfuscated instanceof ByteBinaryTag b) {
                style.decoration(TextDecoration.OBFUSCATED, b.value() == 1 ? State.TRUE : State.FALSE);
            }

            String insertion = compound.getString("insertion");
            if (!insertion.isEmpty()) {
                style.insertion(insertion);
            }

            CompoundBinaryTag clickEvent = compound.getCompound("clickEvent");
            if (clickEvent.size() > 0) {
                style.clickEvent(this.deserializeClickEvent(clickEvent));
            }

            CompoundBinaryTag hoverEvent = compound.getCompound("hoverEvent");
            if (hoverEvent.size() > 0) {
                style.hoverEvent(this.deserializeHoverEvent(hoverEvent));
            }

            return style.build();
        } else {
            return Style.empty();
        }
    }

    private @NotNull ComponentBuilder<?, ?> deserializeTextComponent(@NotNull CompoundBinaryTag compound) {
        String text = compound.getString("text");
        Check.notNull(text, "Text component must have a text field");
        return Component.text().content(text);
    }

    private @NotNull ComponentBuilder<?, ?> deserializeTranslatableComponent(@NotNull CompoundBinaryTag compound) {
        String key = compound.getString("translate");
        Check.notNull(key, "Translatable component must have a translate field");
        TranslatableComponent.Builder builder = Component.translatable().key(key);
        BinaryTag fallback = compound.get("fallback");
        if (fallback instanceof StringBinaryTag s) {
            builder.fallback(s.value());
        }

        ListBinaryTag args = compound.getList("with", BinaryTagTypes.COMPOUND);
        if (args.size() > 0) {
            ArrayList<ComponentLike> list = new ArrayList();
            Iterator var7 = args.iterator();

            while(var7.hasNext()) {
                BinaryTag arg = (BinaryTag)var7.next();
                list.add(this.deserializeComponent((CompoundBinaryTag)arg));
            }

            builder.arguments(list);
        }

        return builder;
    }

    private @NotNull ComponentBuilder<?, ?> deserializeScoreComponent(@NotNull CompoundBinaryTag compound) {
        CompoundBinaryTag scoreCompound = compound.getCompound("score");
        Check.notNull(scoreCompound, "Score component must have a score field");
        String name = scoreCompound.getString("name");
        Check.notNull(name, "Score component score field must have a name field");
        String objective = scoreCompound.getString("objective");
        Check.notNull(objective, "Score component score field must have an objective field");
        ScoreComponent.Builder builder = Component.score().name(name).objective(objective);
        String value = scoreCompound.getString("value");
        if (!value.isEmpty()) {
            builder.value(value);
        }

        return builder;
    }

    private @NotNull ComponentBuilder<?, ?> deserializeSelectorComponent(@NotNull CompoundBinaryTag compound) {
        String selector = compound.getString("selector");
        Check.notNull(selector, "Selector component must have a selector field");
        SelectorComponent.Builder builder = Component.selector().pattern(selector);
        BinaryTag separator = compound.get("separator");
        if (separator != null) {
            builder.separator(this.deserializeAnyComponent(separator));
        }

        return builder;
    }

    private @NotNull ComponentBuilder<?, ?> deserializeKeybindComponent(@NotNull CompoundBinaryTag compound) {
        String keybind = compound.getString("keybind");
        Check.notNull(keybind, "Keybind component must have a keybind field");
        return Component.keybind().keybind(keybind);
    }

    private @NotNull ComponentBuilder<?, ?> deserializeNbtComponent(@NotNull CompoundBinaryTag compound) {
        throw new UnsupportedOperationException("NBTComponent is not implemented yet");
    }

    private @NotNull ClickEvent deserializeClickEvent(@NotNull CompoundBinaryTag compound) {
        String actionName = compound.getString("action");
        Check.notNull(actionName, "Click event must have an action field");
        ClickEvent.Action action = Action.NAMES.value(actionName);
        Check.notNull(action, "Unknown click event action: " + actionName);
        String value = compound.getString("value");
        Check.notNull(value, "Click event must have a value field");
        return ClickEvent.clickEvent(action, value);
    }

    private @NotNull HoverEvent<?> deserializeHoverEvent(@NotNull CompoundBinaryTag compound) {
        String actionName = compound.getString("action");
        Check.notNull(actionName, "Hover event must have an action field");
        CompoundBinaryTag contents = compound.getCompound("contents");
        Check.notNull(contents, "Hover event must have a contents field");
        HoverEvent.Action<?> action = (HoverEvent.Action)net.kyori.adventure.text.event.HoverEvent.Action.NAMES.value(actionName);
        if (action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT) {
            return HoverEvent.showText(this.deserializeComponent(contents));
        } else {
            String type;
            if (action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ITEM) {
                String id = contents.getString("id");
                Check.notNull(id, "Show item hover event must have an id field");
                int count = contents.getInt("count");
                type = contents.getString("tag");
                BinaryTagHolder binaryTag = type.isEmpty() ? null : BinaryTagHolder.binaryTagHolder(type);
                return HoverEvent.showItem(Key.key(id), count, binaryTag);
            } else if (action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ENTITY) {
                CompoundBinaryTag name = contents.getCompound("name");
                Component nameComponent = name.size() == 0 ? null : this.deserializeComponent(name);
                type = contents.getString("type");
                Check.notNull(type, "Show entity hover event must have a type field");
                String id = contents.getString("id");
                Check.notNull(id, "Show entity hover event must have an id field");
                return HoverEvent.showEntity(Key.key(type), UUID.fromString(id), nameComponent);
            } else {
                throw new UnsupportedOperationException("Unknown hover event action: " + actionName);
            }
        }
    }

    private @NotNull CompoundBinaryTag serializeComponent(@NotNull Component component) {
        CompoundBinaryTag.Builder compound = CompoundBinaryTag.builder();
        if (component instanceof TextComponent text) {
            compound.putString("type", "text");
            compound.putString("text", text.content());
        } else if (component instanceof TranslatableComponent translatable) {
            compound.putString("type", "translatable");
            compound.putString("translate", translatable.key());
            String fallback = translatable.fallback();
            if (fallback != null) {
                compound.putString("fallback", fallback);
            }

            List<TranslationArgument> args = translatable.arguments();
            if (!args.isEmpty()) {
                compound.put("with", this.serializeTranslationArgs(args));
            }
        } else if (component instanceof ScoreComponent score) {
            compound.putString("type", "score");
            CompoundBinaryTag.Builder scoreCompound = CompoundBinaryTag.builder();
            scoreCompound.putString("name", score.name());
            scoreCompound.putString("objective", score.objective());
            String value = score.value();
            if (value != null) {
                scoreCompound.putString("value", value);
            }

            compound.put("score", scoreCompound.build());
        } else if (component instanceof SelectorComponent selector) {
            compound.putString("type", "selector");
            compound.putString("selector", selector.pattern());
            Component separator = selector.separator();
            if (separator != null) {
                compound.put("separator", this.serializeComponent(separator));
            }
        } else {
            if (!(component instanceof KeybindComponent)) {
                if (component instanceof NBTComponent) {
                    NBTComponent<?, ?> nbt = (NBTComponent)component;
                    throw new UnsupportedOperationException("NBTComponent is not implemented yet");
                }

                throw new UnsupportedOperationException("Unknown component type: " + component.getClass().getName());
            }

            KeybindComponent keybind = (KeybindComponent)component;
            compound.putString("type", "keybind");
            compound.putString("keybind", keybind.keybind());
        }

        if (!component.children().isEmpty()) {
            ListBinaryTag.Builder<CompoundBinaryTag> children = ListBinaryTag.builder(BinaryTagTypes.COMPOUND);
            Iterator var12 = component.children().iterator();

            while(var12.hasNext()) {
                Component child = (Component)var12.next();
                children.add(this.serializeComponent(child));
            }

            compound.put("extra", children.build());
        }

        compound.put(this.serializeStyle(component.style()));
        return compound.build();
    }

    public @NotNull CompoundBinaryTag serializeStyle(@NotNull Style style) {
        CompoundBinaryTag.Builder compound = CompoundBinaryTag.builder();
        TextColor color = style.color();
        if (color != null) {
            if (color instanceof NamedTextColor) {
                NamedTextColor named = (NamedTextColor)color;
                compound.putString("color", named.toString());
            } else {
                compound.putString("color", color.asHexString());
            }
        }

        Key font = style.font();
        if (font != null) {
            compound.putString("font", font.toString());
        }

        TextDecoration.State bold = style.decoration(TextDecoration.BOLD);
        if (bold != State.NOT_SET) {
            compound.putBoolean("bold", bold == State.TRUE);
        }

        TextDecoration.State italic = style.decoration(TextDecoration.ITALIC);
        if (italic != State.NOT_SET) {
            compound.putBoolean("italic", italic == State.TRUE);
        }

        TextDecoration.State underlined = style.decoration(TextDecoration.UNDERLINED);
        if (underlined != State.NOT_SET) {
            compound.putBoolean("underlined", underlined == State.TRUE);
        }

        TextDecoration.State strikethrough = style.decoration(TextDecoration.STRIKETHROUGH);
        if (strikethrough != State.NOT_SET) {
            compound.putBoolean("strikethrough", strikethrough == State.TRUE);
        }

        TextDecoration.State obfuscated = style.decoration(TextDecoration.OBFUSCATED);
        if (obfuscated != State.NOT_SET) {
            compound.putBoolean("obfuscated", obfuscated == State.TRUE);
        }

        String insertion = style.insertion();
        if (insertion != null) {
            compound.putString("insertion", insertion);
        }

        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null) {
            compound.put("clickEvent", this.serializeClickEvent(clickEvent));
        }

        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            compound.put("hoverEvent", this.serializeHoverEvent(hoverEvent));
        }

        return compound.build();
    }

    private @NotNull BinaryTag serializeTranslationArgs(@NotNull Collection<TranslationArgument> args) {
        ListBinaryTag.Builder<CompoundBinaryTag> argList = ListBinaryTag.builder(BinaryTagTypes.COMPOUND);
        Iterator var3 = args.iterator();

        while(var3.hasNext()) {
            TranslationArgument arg = (TranslationArgument)var3.next();
            argList.add(this.serializeComponent(arg.asComponent()));
        }

        return argList.build();
    }

    private @NotNull BinaryTag serializeClickEvent(@NotNull ClickEvent event) {
        return ((CompoundBinaryTag.Builder)((CompoundBinaryTag.Builder)CompoundBinaryTag.builder().putString("action", event.action().toString())).putString("value", event.value())).build();
    }

    private @NotNull BinaryTag serializeHoverEvent(@NotNull HoverEvent<?> event) {
        CompoundBinaryTag.Builder compound = CompoundBinaryTag.builder();
        compound.putString("action", event.action().toString());
        if (event.action() == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT) {
            Component value = (Component)event.value();
            compound.put("contents", this.serializeComponent(value));
        } else {
            CompoundBinaryTag.Builder itemCompound;
            if (event.action() == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ITEM) {
                HoverEvent.ShowItem value = (HoverEvent.ShowItem)event.value();
                itemCompound = CompoundBinaryTag.builder();
                itemCompound.putString("id", value.item().asString());
                if (value.count() != 1) {
                    itemCompound.putInt("count", value.count());
                }

                BinaryTagHolder tag = value.nbt();
                if (tag != null) {
                    itemCompound.putString("tag", tag.string());
                }

                compound.put("contents", itemCompound.build());
            } else {
                if (event.action() != net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ENTITY) {
                    throw new UnsupportedOperationException("Unknown hover event action: " + String.valueOf(event.action()));
                }

                HoverEvent.ShowEntity value = (HoverEvent.ShowEntity)event.value();
                itemCompound = CompoundBinaryTag.builder();
                Component name = value.name();
                if (name != null) {
                    itemCompound.put("name", this.serializeComponent(name));
                }

                itemCompound.putString("type", value.type().asString());
                itemCompound.putString("id", value.id().toString());
                compound.put("contents", itemCompound.build());
            }
        }

        return compound.build();
    }
}

