package ru.mobileup.samples.core.map.domain

enum class MapTheme(
    val yandexJson: String,
    val googleJson: String
) {
    Bright(
        yandexJson = """
    [
        {
            "types": ["polyline", "polygon", "point"],
            "tags": {"none": []},
            "stylers": {"saturation": 1}
        }
    ]
""",
        googleJson = """
[
  {"elementType":"geometry","stylers":[{"color":"#ffffff"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#ff6b9e"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#ffffff"}]},
  {
    "featureType":"water",
    "elementType":"geometry",
    "stylers":[{"color":"#80e1ff"}]
  },
  {
    "featureType":"road",
    "elementType":"geometry",
    "stylers":[{"color":"#ffd166"}]
  },
  {
    "featureType":"poi.park",
    "elementType":"geometry",
    "stylers":[{"color":"#a8ff96"}]
  }
]
        """
    ),
    Default(
        """
[
  {"featureType":"all","stylers":[{"saturation":0}]}
]
""",
        googleJson = """
            []
        """
    ),
    Dark(
        yandexJson = """
[
  {
    "elements": ["geometry"],
    "stylers": {
      "color": "#1e1e1e"
    }
  },
  {
    "elements": ["geometry.fill"],
    "stylers": {
      "color": "#2b2b2b"
    }
  },
  {
    "elements": ["geometry.outline"],
    "stylers": {
      "color": "#3a3a3a"
    }
  },
  {
    "elements": ["label.text.fill"],
    "stylers": {
      "color": "#aaaaaa"
    }
  },
  {
    "elements": ["label.text.outline"],
    "stylers": {
      "color": "#000000",
      "opacity": 0.8
    }
  },
  {
    "tags": {
      "any": ["road"]
    },
    "elements": ["geometry"],
    "stylers": {
      "color": "#444444"
    }
  },
  {
    "tags": {
      "any": ["road_minor", "road_unclassified", "path"]
    },
    "stylers": {
      "visibility": "off"
    }
  },
  {
    "tags": {
      "any": ["land"]
    },
    "stylers": {
      "color": "#1a1a1a"
    }
  },
  {
    "tags": {
      "any": ["vegetation", "park", "national_park"]
    },
    "stylers": {
      "color": "#1f3f1f"
    }
  },
  {
    "tags": {
      "any": ["water"]
    },
    "stylers": {
      "color": "#0f1a2b"
    }
  },
  {
    "tags": {
      "any": ["building"]
    },
    "stylers": {
      "color": "#333333"
    }
  },
  {
    "tags": {
      "any": ["poi", "transit"]
    },
    "stylers": {
      "visibility": "off"
    }
  },
  {
    "elements": ["label.icon"],
    "stylers": {
      "visibility": "off"
    }
  }
]
""",
        googleJson = """
           [
  {"elementType":"geometry","stylers":[{"color":"#1d2c4d"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#8ec3b9"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#1a3646"}]},
  {
    "featureType":"road",
    "elementType":"geometry",
    "stylers":[{"color":"#304a7d"}]
  },
  {
    "featureType":"water",
    "elementType":"geometry",
    "stylers":[{"color":"#17263c"}]
  }
]
        """
    ),
}