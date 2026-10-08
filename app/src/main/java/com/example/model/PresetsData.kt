package com.example.model

object PresetsData {

  fun createDefaultProject(): KitProject {
    return createCyberpunkNeon()
  }

  fun getAllPresets(): List<Pair<String, () -> KitProject>> = listOf(
    "Cyber Neon Kit" to ::createCyberpunkNeon,
    "Nerazzurri Stripes" to ::createNerazzurri,
    "Blaugrana Classic" to ::createBlaugrana,
    "Rossoneri Strike" to ::createRossoneri,
    "Galácticos Pure White" to ::createRealBlanc,
    "Celeste Sash" to ::createCelesteSash,
    "Bianconeri Bold" to ::createBianconeri,
    "Azzurri Renaissance" to ::createAzzurri
  )

  fun createCyberpunkNeon(): KitProject {
    val shirtLayers = listOf(
      KitLayer(
        name = "Dark Carbon Base",
        type = LayerType.BASE_DESIGN,
        primaryColor = 0xFF14171EL
      ),
      KitLayer(
        name = "Neon Hex Honeycomb",
        type = LayerType.PATTERN,
        patternType = KitPatternType.HEX_HONEYCOMB,
        patternFrequency = 10,
        primaryColor = 0xFF14171EL,
        secondaryColor = 0x2522C55EL,
        opacity = 0.85f
      ),
      KitLayer(
        name = "Neon Shoulder Panels",
        type = LayerType.SHOULDER_PANELS,
        primaryColor = 0xFF22C55EL,
        mask = LayerMask.FULL
      ),
      KitLayer(
        name = "Side Ventilation Strips",
        type = LayerType.SIDE_PANELS,
        primaryColor = 0xFF16A34AL,
        mask = LayerMask.SIDE_LEFT
      ),
      KitLayer(
        name = "Sleeve Cuffs Neon",
        type = LayerType.SLEEVE_CUFFS,
        primaryColor = 0xFF22C55EL
      ),
      KitLayer(
        name = "Club Shield Crest",
        type = LayerType.LOGO,
        logoType = LogoType.CREST_SHIELD,
        primaryColor = 0xFF22C55EL,
        secondaryColor = 0xFF0F172AL,
        tertiaryColor = 0xFFFACC15L,
        transform = TransformState(x = -28f, y = -18f, scaleX = 0.9f, scaleY = 0.9f)
      ),
      KitLayer(
        name = "Brand Athletic Swoop",
        type = LayerType.LOGO,
        logoType = LogoType.BRAND_SWOOSH,
        primaryColor = 0xFFFFFFFFL,
        transform = TransformState(x = 28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)
      ),
      KitLayer(
        name = "Chest Sponsor",
        type = LayerType.LOGO,
        logoType = LogoType.SPONSOR_TECH,
        primaryColor = 0xFFFFFFFFL,
        transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)
      ),
      KitLayer(
        name = "Squad Name",
        type = LayerType.TEXT,
        text = "HAALAND",
        textFont = TextFont.ATHLETIC_BLOCK,
        primaryColor = 0xFF22C55EL,
        strokeWidth = 2f,
        strokeColor = 0xFF000000L,
        transform = TransformState(x = 0f, y = -36f, scaleX = 1.0f, scaleY = 1.0f),
        mask = LayerMask.TORSO_BACK
      ),
      KitLayer(
        name = "Squad Number",
        type = LayerType.TEXT,
        text = "9",
        textFont = TextFont.ATHLETIC_BLOCK,
        textSize = 54f,
        primaryColor = 0xFF22C55EL,
        strokeWidth = 3f,
        strokeColor = 0xFF000000L,
        transform = TransformState(x = 0f, y = -6f, scaleX = 1.2f, scaleY = 1.2f),
        mask = LayerMask.TORSO_BACK
      )
    )

    val shortsLayers = listOf(
      KitLayer(name = "Shorts Dark Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF14171EL),
      KitLayer(name = "Shorts Neon Side Trim", type = LayerType.SIDE_PANELS, primaryColor = 0xFF22C55EL),
      KitLayer(name = "Shorts Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFF22C55EL, transform = TransformState(x = 26f, y = 18f, scaleX = 0.6f, scaleY = 0.6f)),
      KitLayer(name = "Shorts Number", type = LayerType.TEXT, text = "9", primaryColor = 0xFFFFFFFFL, transform = TransformState(x = -26f, y = 18f, scaleX = 0.6f, scaleY = 0.6f))
    )

    val socksLayers = listOf(
      KitLayer(name = "Socks Carbon Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF14171EL),
      KitLayer(name = "Socks Top Ribbing", type = LayerType.PATTERN, patternType = KitPatternType.STRIPES_HORIZONTAL, patternFrequency = 3, primaryColor = 0xFF14171EL, secondaryColor = 0xFF22C55EL)
    )

    return KitProject(
      name = "Cyberpunk Neon 2027",
      teamName = "Cyber FC",
      collarStyle = CollarStyle.V_NECK,
      collarColor = 0xFF22C55EL,
      collarTrimColor = 0xFF14171EL,
      shirtLayers = shirtLayers,
      shortsLayers = shortsLayers,
      socksLayers = socksLayers
    )
  }

  fun createNerazzurri(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Black Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0A0A0AL),
      KitLayer(
        name = "Blue Stripes",
        type = LayerType.PATTERN,
        patternType = KitPatternType.STRIPES_VERTICAL,
        patternFrequency = 7,
        primaryColor = 0xFF0A0A0AL,
        secondaryColor = 0xFF0244B2L
      ),
      KitLayer(name = "Gold Shield Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFFEAB308L, secondaryColor = 0xFF0244B2L, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Mark", type = LayerType.LOGO, logoType = LogoType.BRAND_SWOOSH, primaryColor = 0xFFEAB308L, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Chest Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_AIRWAYS, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "LAUTARO", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "10", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFFEAB308L, strokeWidth = 2f, strokeColor = 0xFF000000L, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Milano Nerazzurri 26/27",
      teamName = "FC Internazionale",
      collarStyle = CollarStyle.V_NECK,
      collarColor = 0xFF0A0A0AL,
      collarTrimColor = 0xFFEAB308L,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "Black Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0A0A0AL)),
      socksLayers = listOf(KitLayer(name = "Blue Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0244B2L))
    )
  }

  fun createBlaugrana(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Garnet Red Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF85142AL),
      KitLayer(name = "Royal Blue Stripes", type = LayerType.PATTERN, patternType = KitPatternType.STRIPES_VERTICAL, patternFrequency = 5, primaryColor = 0xFF85142AL, secondaryColor = 0xFF003882L),
      KitLayer(name = "Blaugrana Crest", type = LayerType.LOGO, logoType = LogoType.CREST_ROUNDEL, primaryColor = 0xFFEAB308L, secondaryColor = 0xFF85142AL, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Swoosh", type = LayerType.LOGO, logoType = LogoType.BRAND_SWOOSH, primaryColor = 0xFFEAB308L, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Streaming Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_STREAMING, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "YAMAL", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFFEAB308L, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "19", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFFEAB308L, strokeWidth = 2f, strokeColor = 0xFF003882L, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Blaugrana Barcelona Home",
      teamName = "FC Barcelona",
      collarStyle = CollarStyle.CREW_NECK,
      collarColor = 0xFFEAB308L,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "Blue Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFF003882L)),
      socksLayers = listOf(KitLayer(name = "Red-Blue Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFF85142AL))
    )
  }

  fun createRossoneri(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Dark Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0A0A0AL),
      KitLayer(name = "Crimson Stripes", type = LayerType.PATTERN, patternType = KitPatternType.STRIPES_VERTICAL, patternFrequency = 7, primaryColor = 0xFF0A0A0AL, secondaryColor = 0xFFDC2626L),
      KitLayer(name = "Star Shield Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFFFFFFFFL, secondaryColor = 0xFFDC2626L, tertiaryColor = 0xFFEAB308L, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Triple Stripes Mark", type = LayerType.LOGO, logoType = LogoType.BRAND_TRIPLE_STRIPES, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Fly Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_AIRWAYS, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "LEÃO", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "10", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFFFFFFFFL, strokeWidth = 2f, strokeColor = 0xFF0A0A0AL, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Rossoneri Strike 26/27",
      teamName = "AC Milan",
      collarStyle = CollarStyle.V_NECK,
      collarColor = 0xFF0A0A0AL,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "Black Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0A0A0AL)),
      socksLayers = listOf(KitLayer(name = "Black Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0A0A0AL))
    )
  }

  fun createRealBlanc(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Pure Crisp White", type = LayerType.BASE_DESIGN, primaryColor = 0xFFF8FAFCL),
      KitLayer(name = "Gold Shoulder Trim", type = LayerType.SHOULDER_PANELS, primaryColor = 0xFFEAB308L),
      KitLayer(name = "Gold Crown Crest", type = LayerType.LOGO, logoType = LogoType.CREST_ROUNDEL, primaryColor = 0xFFEAB308L, secondaryColor = 0xFF1E3A8AL, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Mark", type = LayerType.LOGO, logoType = LogoType.BRAND_TRIPLE_STRIPES, primaryColor = 0xFF1E3A8AL, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Chest Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_AIRWAYS, primaryColor = 0xFF1E3A8AL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "MBAPPÉ", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFF1E3A8AL, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "9", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFF1E3A8AL, strokeWidth = 2f, strokeColor = 0xFFEAB308L, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Madrid Galácticos Blanc",
      teamName = "Real Madrid CF",
      collarStyle = CollarStyle.POLO_BUTTON,
      collarColor = 0xFFF8FAFCL,
      collarTrimColor = 0xFFEAB308L,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "White Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFFF8FAFCL)),
      socksLayers = listOf(KitLayer(name = "White Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFFF8FAFCL))
    )
  }

  fun createCelesteSash(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Sky Blue Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFF38BDF8L),
      KitLayer(name = "White Diagonal Sash", type = LayerType.PATTERN, patternType = KitPatternType.SASH_DIAGONAL, primaryColor = 0xFF38BDF8L, secondaryColor = 0xFFFFFFFFL),
      KitLayer(name = "Shield Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFF0284C7L, secondaryColor = 0xFFFFFFFFL, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Diamond", type = LayerType.LOGO, logoType = LogoType.BRAND_DIAMOND, primaryColor = 0xFF0F172AL, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Tech Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_TECH, primaryColor = 0xFF0F172AL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "MESSI", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFF0F172AL, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "10", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFF0F172AL, strokeWidth = 2f, strokeColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Celeste Diagonal Sash",
      teamName = "Celeste Sporting",
      collarStyle = CollarStyle.V_NECK,
      collarColor = 0xFFFFFFFFL,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "Black Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFF0F172AL)),
      socksLayers = listOf(KitLayer(name = "Sky Blue Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFF38BDF8L))
    )
  }

  fun createBianconeri(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Pure White Base", type = LayerType.BASE_DESIGN, primaryColor = 0xFFFFFFFFL),
      KitLayer(name = "Zebra Vertical Stripes", type = LayerType.PATTERN, patternType = KitPatternType.STRIPES_VERTICAL, patternFrequency = 7, primaryColor = 0xFFFFFFFFL, secondaryColor = 0xFF0F172AL),
      KitLayer(name = "Shield Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFFEAB308L, secondaryColor = 0xFF0F172AL, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Mark", type = LayerType.LOGO, logoType = LogoType.BRAND_TRIPLE_STRIPES, primaryColor = 0xFFEAB308L, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Sponsor Automotive", type = LayerType.LOGO, logoType = LogoType.SPONSOR_AUTOMOTIVE, primaryColor = 0xFFEAB308L, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "DEL PIERO", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFFEAB308L, strokeWidth = 2f, strokeColor = 0xFF0F172AL, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "10", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFFEAB308L, strokeWidth = 3f, strokeColor = 0xFF0F172AL, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Bianconeri Zebra Classic",
      teamName = "Juventus FC",
      collarStyle = CollarStyle.CREW_NECK,
      collarColor = 0xFF0F172AL,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "White Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFFFFFFFFL)),
      socksLayers = listOf(KitLayer(name = "White Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFFFFFFFFL))
    )
  }

  fun createAzzurri(): KitProject {
    val shirtLayers = listOf(
      KitLayer(name = "Mediterranean Blue", type = LayerType.BASE_DESIGN, primaryColor = 0xFF1D4ED8L),
      KitLayer(name = "Baroque Renaissance Pattern", type = LayerType.PATTERN, patternType = KitPatternType.DIAMONDS, patternFrequency = 12, primaryColor = 0xFF1D4ED8L, secondaryColor = 0x251E3A8AL),
      KitLayer(name = "Gold Shield Crest", type = LayerType.LOGO, logoType = LogoType.CREST_SHIELD, primaryColor = 0xFFFACC15L, secondaryColor = 0xFF1E3A8AL, tertiaryColor = 0xFFDC2626L, transform = TransformState(x = -28f, y = -18f, scaleX = 0.85f, scaleY = 0.85f)),
      KitLayer(name = "Brand Cat", type = LayerType.LOGO, logoType = LogoType.BRAND_CAT, primaryColor = 0xFFFACC15L, transform = TransformState(x = 28f, y = -18f, scaleX = 0.8f, scaleY = 0.8f)),
      KitLayer(name = "Crypto Sponsor", type = LayerType.LOGO, logoType = LogoType.SPONSOR_CRYPTO, primaryColor = 0xFFFFFFFFL, transform = TransformState(x = 0f, y = 8f, scaleX = 1.3f, scaleY = 1.3f)),
      KitLayer(name = "Player Name", type = LayerType.TEXT, text = "BARELLA", textFont = TextFont.ATHLETIC_BLOCK, primaryColor = 0xFFFACC15L, transform = TransformState(x = 0f, y = -36f), mask = LayerMask.TORSO_BACK),
      KitLayer(name = "Player Number", type = LayerType.TEXT, text = "23", textFont = TextFont.ATHLETIC_BLOCK, textSize = 54f, primaryColor = 0xFFFACC15L, strokeWidth = 2f, strokeColor = 0xFF1E3A8AL, transform = TransformState(x = 0f, y = -6f), mask = LayerMask.TORSO_BACK)
    )
    return KitProject(
      name = "Azzurri Renaissance 2026",
      teamName = "Nazionale Azzurra",
      collarStyle = CollarStyle.V_NECK,
      collarColor = 0xFF1E3A8AL,
      collarTrimColor = 0xFFFACC15L,
      shirtLayers = shirtLayers,
      shortsLayers = listOf(KitLayer(name = "White Shorts", type = LayerType.BASE_DESIGN, primaryColor = 0xFFF8FAFCL)),
      socksLayers = listOf(KitLayer(name = "Blue Socks", type = LayerType.BASE_DESIGN, primaryColor = 0xFF1D4ED8L))
    )
  }
}
