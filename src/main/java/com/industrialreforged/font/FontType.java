package com.industrialreforged.font;

import com.industrialreforged.IndustrialReforged;
import java.util.List;

public enum FontType {
   OpenSans_ExtraBold(new FontContainer("extra", 32, IndustrialReforged.location("fonts/opensans_extrabold.ttf"))),
   OpenSans_Italic(new FontContainer("italic", 32, IndustrialReforged.location("fonts/opensans_italic.ttf"))),
   OpenSans_Regular(new FontContainer("regular", 32, IndustrialReforged.location("fonts/opensans_regular.ttf"))),
   BoldItalic(new FontContainer("BoldItalic", 32, IndustrialReforged.location("fonts/opensans-bolditalic.ttf"))),
   SemiBold(new FontContainer("SemiBold", 32, IndustrialReforged.location("fonts/opensans_semibold.ttf"))),
   BoldItalicText(new FontContainer("BoldItalic", 20, IndustrialReforged.location("fonts/opensans-bolditalic.ttf"))),
   OpenSans_Bold(new FontContainer("Bold", 32, IndustrialReforged.location("fonts/opensans_bold.ttf")));

   private final FontContainer container;

   FontType(FontContainer container) {
      this.container = container;
   }

   public FontContainer getFontContainer() {
      return this.container;
   }

   public float width(String text) {
      return this.container.width(text);
   }

   public float width(String text, float scale) {
      return this.width(text) * scale;
   }

   public float height() {
      return this.container.height() / 1.25F;
   }

   public float height(float scale) {
      return this.height() * scale;
   }

   public List<String> split(String text, float scale, float width) {
      return this.container.split(text, scale, width);
   }
}
