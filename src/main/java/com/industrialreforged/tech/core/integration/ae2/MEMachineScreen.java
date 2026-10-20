package com.industrialreforged.tech.core.integration.ae2;

import com.industrialreforged.IndustrialReforged;
import com.industrialreforged.tech.core.IR;
import com.industrialreforged.utils.NumberUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * ME machine GUI: every recipe of the machine as an icon (pages of 9×5, search by name), a click
 * switches the recipe on or off for the ME network; switched-off recipes are dimmed.
 *
 * <p>Inside the window there is no text that grows with the language: the title is cut to fit, the
 * search box has its own row, "all on/off" are icon tabs on the right and the status lives in the black
 * panel on the left, as wide as its text (the energy panel of the IR machines).
 */
public class MEMachineScreen extends AbstractContainerScreen<MEMachineMenu> {
   private static final ResourceLocation TEXTURE = IR.texture("screen/me_machine");
   private static final int COLS = 9;
   private static final int ROWS = 5;
   private static final int PAGE_SIZE = COLS * ROWS;
   private static final int GRID_X = 8;
   private static final int GRID_Y = 32;
   private static final int BOTTOM_Y = 124;
   /** Check and cross icons of the tabs, 16×16 each, in the spare part of the texture. */
   private static final int ICON_U = 200;
   private static final int PANEL_LABEL = 0xA0A0A0;
   private static final int PANEL_VALUE = 0x00B61E;
   private EditBox search;
   private int page;
   private List<MEMachineRecipe> filtered = List.of();
   @Nullable
   private Map<ResourceLocation, MEMachineRecipe> filteredFrom;
   private String filteredQuery = "";

   public MEMachineScreen(MEMachineMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
      this.imageWidth = 176;
      this.imageHeight = 145;
   }

   @Override
   protected void setInitialFocus() {
      this.setFocused(null);
   }

   @Override
   protected void init() {
      super.init();
      this.search = new EditBox(this.font, this.leftPos + 8, this.topPos + 17, 160, 12, Component.translatable("gui.industrialreforged.me_machine.search"));
      this.search.setHint(Component.translatable("gui.industrialreforged.me_machine.search").withStyle(ChatFormatting.DARK_GRAY));
      this.search.setMaxLength(64);
      this.search.setValue(this.filteredQuery);
      this.search.setResponder(value -> this.page = 0);
      this.addRenderableWidget(new MEPowerButton(this.leftPos + this.imageWidth - 42, this.topPos + 2, () -> this.menu.enabled,
         button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0)));

      if (this.menu.entity.supportsCableOnly()) {
         this.addRenderableWidget(new MEPowerSourceButton(this.leftPos + this.imageWidth - 68, this.topPos + 2, () -> this.menu.cableOnly,
            button -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1)));
      }
      this.addRenderableWidget(this.search);
      this.addRenderableWidget(Button.builder(Component.literal("<"), button -> this.turnPage(-1)).bounds(this.leftPos + 7, this.topPos + BOTTOM_Y, 20, 14).build());
      this.addRenderableWidget(Button.builder(Component.literal(">"), button -> this.turnPage(1)).bounds(this.leftPos + 149, this.topPos + BOTTOM_Y, 20, 14).build());
      this.addTab(0, "gui.industrialreforged.me_machine.enable_all", MEMachineTogglePayload.Action.ENABLE_ALL);
      this.addTab(1, "gui.industrialreforged.me_machine.disable_all", MEMachineTogglePayload.Action.DISABLE_ALL);
   }

   /** Icon tab stuck to the right edge of the window (as the filter's mode tab); the icon is drawn in render. */
   private void addTab(int index, String tooltip, MEMachineTogglePayload.Action action) {
      this.addRenderableWidget(
         Button.builder(Component.empty(), button -> this.send(action, null))
            .bounds(this.leftPos + this.imageWidth - 1, this.topPos + 17 + index * 22, 20, 20)
            .tooltip(Tooltip.create(Component.translatable(tooltip)))
            .build()
      );
   }

   private List<MEMachineRecipe> visibleRecipes() {
      Map<ResourceLocation, MEMachineRecipe> all = this.menu.entity.recipes();
      String query = this.search == null ? "" : this.search.getValue().trim().toLowerCase(Locale.ROOT);
      if (all != this.filteredFrom || !query.equals(this.filteredQuery)) {
         List<MEMachineRecipe> list = new ArrayList<>();
         for (MEMachineRecipe recipe : all.values()) {
            if (matches(recipe, query)) {
               list.add(recipe);
            }
         }

         this.filtered = list;
         this.filteredFrom = all;
         this.filteredQuery = query;
      }

      int pages = this.pageCount();
      if (this.page >= pages) {
         this.page = pages - 1;
      }

      return this.filtered;
   }

   /** Search over output names, input names and the recipe id. */
   private static boolean matches(MEMachineRecipe recipe, String query) {
      if (query.isEmpty() || recipe.id().toString().contains(query)) {
         return true;
      }

      for (ItemStack output : recipe.outputs()) {
         if (output.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
         }
      }

      for (ItemStack input : recipe.input().getItems()) {
         if (input.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
         }
      }

      return false;
   }

   private int pageCount() {
      return Math.max(1, (this.filtered.size() + PAGE_SIZE - 1) / PAGE_SIZE);
   }

   private void turnPage(int delta) {
      this.visibleRecipes();
      this.page = Math.floorMod(this.page + delta, this.pageCount());
   }

   private void send(MEMachineTogglePayload.Action action, @Nullable ResourceLocation recipe) {
      IndustrialReforged.sendToServer(new MEMachineTogglePayload(this.menu.containerId, action, recipe));
   }

   @Nullable
   private MEMachineRecipe recipeAt(double mouseX, double mouseY) {
      int col = (int)Math.floor((mouseX - this.leftPos - GRID_X + 1) / 18.0);
      int row = (int)Math.floor((mouseY - this.topPos - GRID_Y + 1) / 18.0);
      if (col < 0 || col >= COLS || row < 0 || row >= ROWS) {
         return null;
      }

      List<MEMachineRecipe> recipes = this.visibleRecipes();
      int index = this.page * PAGE_SIZE + row * COLS + col;
      return index < recipes.size() ? recipes.get(index) : null;
   }

   @Override
   protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
      graphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      List<MEMachineRecipe> recipes = this.visibleRecipes();
      int start = this.page * PAGE_SIZE;
      for (int i = 0; i < PAGE_SIZE && start + i < recipes.size(); i++) {
         MEMachineRecipe recipe = recipes.get(start + i);
         int x = this.leftPos + GRID_X + (i % COLS) * 18;
         int y = this.topPos + GRID_Y + (i / COLS) * 18;
         graphics.renderItem(recipe.icon(), x, y);
         graphics.renderItemDecorations(this.font, recipe.icon(), x, y);
         if (!this.menu.entity.isEnabled(recipe.id())) {
            // above the stack size (drawn at z 200), so the digits are dimmed too
            graphics.fill(x, y, x + 16, y + 16, 250, 0xB0303030);
         }
      }

      int enabled = 0;
      Map<ResourceLocation, MEMachineRecipe> all = this.menu.entity.recipes();
      for (ResourceLocation id : all.keySet()) {
         if (this.menu.entity.isEnabled(id)) {
            enabled++;
         }
      }

      renderStatusPanel(
         graphics,
         this.font,
         this.leftPos,
         this.topPos,
         List.of(
            Component.translatable("gui.industrialreforged.me_machine.buffer"),
            bufferValue(this.menu.buffer, this.menu.entity.getAEMaxPower()),
            Component.translatable("gui.industrialreforged.me_machine.operations"),
            Component.literal(this.menu.lastOperations + " / " + AbstractMENodeBlockEntity.OPERATIONS_PER_TICK),
            Component.translatable("gui.industrialreforged.me_machine.recipes"),
            Component.literal(enabled + " / " + all.size())
         )
      );
   }

   static Component bufferValue(double buffer, double max) {
      return Component.literal(NumberUtils.formatToSi((long)buffer) + " / " + NumberUtils.formatToSi((long)max));
   }

   /**
    * Black panel left of the window (the energy panel of the IR machines): {@code lines} go label, value,
    * label, value… — a label and its number on separate lines keep the panel narrow enough to stay on a
    * small screen in any language.
    */
   static void renderStatusPanel(GuiGraphics graphics, Font font, int leftPos, int topPos, List<Component> lines) {
      int width = 0;
      for (Component line : lines) {
         width = Math.max(width, font.width(line));
      }

      width += 9;
      int pairs = (lines.size() + 1) / 2;
      int x = leftPos - width - 3;
      graphics.fill(x, topPos, x + width, topPos + lines.size() * 9 + (pairs - 1) * 3 + 8, 0xFF000000);
      int y = topPos + 4;
      for (int i = 0; i < lines.size(); i++) {
         graphics.drawString(font, lines.get(i), x + 4, y, i % 2 == 0 ? PANEL_LABEL : PANEL_VALUE);
         y += i % 2 == 0 ? 9 : 12;
      }
   }

   /** The title, cut with "…" when a long translation would not fit the window. */
   static String fitTitle(Font font, Component title, int width) {
      String text = title.getString();
      return font.width(text) <= width ? text : font.plainSubstrByWidth(text, width - font.width("…")) + "…";
   }

   @Override
   protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
      graphics.drawString(this.font, fitTitle(this.font, this.title, this.imageWidth - 80), 8, 6, 0x404040, false);
      String pages = (this.page + 1) + " / " + this.pageCount();
      graphics.drawString(this.font, pages, (this.imageWidth - this.font.width(pages)) / 2, BOTTOM_Y + 3, 0x404040, false);
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      for (int i = 0; i < 2; i++) {
         graphics.blit(TEXTURE, this.leftPos + this.imageWidth + 1, this.topPos + 19 + i * 22, ICON_U + i * 16, 0, 16, 16);
      }

      MEMachineRecipe hovered = this.recipeAt(mouseX, mouseY);
      if (hovered != null) {
         int col = (int)Math.floor((mouseX - this.leftPos - GRID_X + 1) / 18.0);
         int row = (int)Math.floor((mouseY - this.topPos - GRID_Y + 1) / 18.0);
         int x = this.leftPos + GRID_X + col * 18;
         int y = this.topPos + GRID_Y + row * 18;
         graphics.fill(x, y, x + 16, y + 16, 300, 0x80FFFFFF);
         graphics.renderComponentTooltip(this.font, this.tooltip(hovered), mouseX, mouseY);
      } else {
         this.renderTooltip(graphics, mouseX, mouseY);
      }
   }

   private List<Component> tooltip(MEMachineRecipe recipe) {
      List<Component> lines = new ArrayList<>();
      for (ItemStack output : recipe.outputs()) {
         lines.add(Component.literal(output.getCount() + "× ").append(output.getHoverName()));
      }

      ItemStack[] inputs = recipe.input().getItems();
      Component input = Component.literal(recipe.inputCount() + "× ").append(inputs[0].getHoverName());
      if (inputs.length > 1) {
         input = Component.translatable("gui.industrialreforged.me_machine.input_variants", input, inputs.length - 1);
      }

      lines.add(Component.translatable("gui.industrialreforged.me_machine.input", input).withStyle(ChatFormatting.GRAY));
      lines.add(
         Component.translatable("gui.industrialreforged.me_machine.cost", NumberUtils.formatToSi(recipe.aeCost()))
            .withStyle(ChatFormatting.GRAY)
      );
      lines.add(
         this.menu.entity.isEnabled(recipe.id())
            ? Component.translatable("gui.industrialreforged.me_machine.enabled").withStyle(ChatFormatting.GREEN)
            : Component.translatable("gui.industrialreforged.me_machine.disabled").withStyle(ChatFormatting.RED)
      );
      return lines;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      MEMachineRecipe recipe = this.recipeAt(mouseX, mouseY);
      if (recipe != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
         this.send(MEMachineTogglePayload.Action.TOGGLE, recipe.id());
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         return true;
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (scrollY != 0) {
         this.turnPage(scrollY > 0 ? -1 : 1);
         return true;
      }

      return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      // typing in the search box must not close the screen on the inventory key
      if (this.search.isFocused() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
         return this.search.keyPressed(keyCode, scanCode, modifiers) || this.search.canConsumeInput();
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }
}
