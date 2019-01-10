package com.hub.toolbox.mtg.sanitalia.constants
enum class Group {
    HOME_SERVICES,
    DOCTOR,
    STRUCTURE
}

val GroupsValues = hashMapOf(
        Pair(Group.HOME_SERVICES, 0),
        Pair(Group.DOCTOR, 1),
        Pair(Group.STRUCTURE, 2)
)

val HomeServiceCategories = hashMapOf(
        Pair("Fisioterapista", 0),
        Pair("Operatore Socio Sanitario", 1),
        Pair("Infermiere", 2),
        Pair("Assistente Anziani",3)
)

val PhysiotherapistSpecs = hashMapOf(
        Pair("Trattamento Fisioterapico", 0),
        Pair("Bendaggio Funzionale", 1),
        Pair("Chinesiterapia", 2),
        Pair("Fiosioterapia Sportiva", 3),
        Pair("Rieducazione Posturale",4),
        Pair("Linfodrenaggio Manuale", 5),
        Pair("Manipolazione Fasciale", 6),
        Pair("Manipolazione Vertebrale", 7),
        Pair("Massaggio Decontratturante", 8),
        Pair("Riabilitazione Neuromotoria", 9),
        Pair("Riabilitazione Ortopedica", 10),
        Pair("Terapia Manuale", 11),
        Pair("Terapia Cranio Sacrale", 12)
)

val NurseSpecs = hashMapOf(
        Pair("Iniezioni Intramuscolari", 0),
        Pair("Somministrazione Medicinali", 1),
        Pair("Consegna Medicinali a domicilio", 2),
        Pair("Iniezioni di insulina", 3),
        Pair("Medicazioni semplici o complesse", 4),
        Pair("Medicazione piaghe da decubito",5),
        Pair("Catererismi (cambio catetere)", 6),
        Pair("Flebo", 7),
        Pair("Prelievo di sangue con consegna", 8),
        Pair("Iniezioni", 9),
        Pair("Clistere", 10),
        Pair("Prelievo da catetere per esame urine", 11),
        Pair("Trasporta prelievo ambulatorio", 12)
)

val DoctorsSpecs = hashMapOf(
        Pair("Allergologia", 0),
        Pair("Allenamento ad alta quota", 1),
        Pair("Anatomia patologica", 2),
        Pair("Andrologia", 3),
        Pair("Anestesia e rianimazione", 4),
        Pair("Angiologia", 5),
        Pair("Audiologia", 6),
        Pair("Auxologia", 7),
        Pair("Biomedicina", 8),
        Pair("Cardiochirurgia", 9),
        Pair("Cardiologia", 10),
        Pair("Chirurgia", 11),
        Pair("Chirurgia vascolare", 12),
        Pair("Chirurgia odontostomatologica", 13),
        Pair("Dermatologia", 14),
        Pair("Dietetica", 15),
        Pair("Ematologia", 16),
        Pair("Embriologia", 17),
        Pair("Endocrinologia", 18),
        Pair("Epatologia", 19),
        Pair("Epidemiologia", 20),
        Pair("Flebologia", 21),
        Pair("Foniatria", 22),
        Pair("Gastroenterologia", 23),
        Pair("Genetica", 24),
        Pair("Geriatria", 25),
        Pair("Ginecologia e ostetricia", 26),
        Pair("Igiene e medicina preventiva", 27),
        Pair("Immunologia", 28),
        Pair("Infettivologia", 29),
        Pair("Istologia", 30),
        Pair("Logopedia", 31),
        Pair("Medicina aeronautica", 32),
        Pair("Medicina delle catastrofi", 33),
        Pair("Medicina del lavoro", 34),
        Pair("Medicina d'urgenza", 35),
        Pair("Medicina di comunità", 36),
        Pair("Medicina estetica", 37),
        Pair("Medicina fisica e riabilitativa", 38),
        Pair("Medicina interna", 39),
        Pair("Medicina nucleare", 40),
        Pair("Medicina pubblica", 41),
        Pair("Medicina generale", 42),
        Pair("Medicina legale", 43),
        Pair("Medicina nucleare", 44),
        Pair("Medicina spaziale", 45),
        Pair("Medicina sportiva", 46),
        Pair("Medicina subacquea", 47),
        Pair("Nanomedicina", 48),
        Pair("Neuroriabilitazione", 49),
        Pair("Nefrologia", 50),
        Pair("Neonatologia", 51),
        Pair("Neurologia", 52),
        Pair("Neuroscienze", 53),
        Pair("Odontoiatria", 54),
        Pair("Oftalmologia", 55),
        Pair("Oncologia", 56),
        Pair("Ortopedia", 57),
        Pair("Otorinolaringoiatria", 58),
        Pair("Pediatria", 59),
        Pair("Pneumologia", 60),
        Pair("Professioni mediche", 61),
        Pair("Psichiatria", 62),
        Pair("Radiologia", 63),
        Pair("Radioterapia", 64),
        Pair("Reumatologia", 65),
        Pair("Riabilitazione equestre", 66),
        Pair("Senologia", 67),
        Pair("Sessuologia", 68),
        Pair("Tabaccologia", 69),
        Pair("Tricologia", 70),
        Pair("Traumatologia", 71),
        Pair("Urologia", 72),
        Pair("Venereologia", 73)
)

val Provinces = hashMapOf(
        Pair("AG", Pair("Agrigento", "Sicilia")),
        Pair("AL", Pair("Alessandria", "Piemonte")),
        Pair("AN", Pair("Ancona", "Marche")),
        Pair("AO", Pair("Aosta", "Valle-d-Aosta")),
        Pair("AQ", Pair("L'Aquila", "Abruzzo")),
        Pair("AR", Pair("Arezzo", "Toscana")),
        Pair("AP", Pair("Ascoli-Piceno", "Marche")),
        Pair("AT", Pair("Asti", "Piemonte")),
        Pair("AV", Pair("Avellino", "Campania")),
        Pair("BA", Pair("Bari", "Puglia")),
        Pair("BT", Pair("Barletta-Andria-Trani", "Puglia")),
        Pair("BL", Pair("Belluno", "Veneto")),
        Pair("BN", Pair("Benevento", "Campania")),
        Pair("BG", Pair("Bergamo", "Lombardia")),
        Pair("BI", Pair("Biella", "Piemonte")),
        Pair("BO", Pair("Bologna", "Emilia-Romagna")),
        Pair("BZ", Pair("Bolzano", "Trentino-Alto-Adige")),
        Pair("BS", Pair("Brescia", "Lombardia")),
        Pair("BR", Pair("Brindisi", "Puglia")),
        Pair("CA", Pair("Cagliari", "Sardegna")),
        Pair("CL", Pair("Caltanissetta", "Sicilia")),
        Pair("CB", Pair("Campobasso", "Molise")),
        Pair("CI", Pair("Carbonia-Iglesias", "Sardegna")),
        Pair("CE", Pair("Caserta", "Campania")),
        Pair("CT", Pair("Catania", "Sicilia")),
        Pair("CZ", Pair("Catanzaro", "Calabria")),
        Pair("CH", Pair("Chieti", "Abruzzo")),
        Pair("CO", Pair("Como", "Lombardia")),
        Pair("CS", Pair("Cosenza", "Calabria")),
        Pair("CR", Pair("Cremona", "Lombardia")),
        Pair("KR", Pair("Crotone", "Calabria")),
        Pair("CN", Pair("Cuneo", "Piemonte")),
        Pair("EN", Pair("Enna", "Sicilia")),
        Pair("FM", Pair("Fermo", "Marche")),
        Pair("FE", Pair("Ferrara", "Emilia-Romagna")),
        Pair("FI", Pair("Firenze", "Toscana")),
        Pair("FG", Pair("Foggia", "Puglia")),
        Pair("FC", Pair("Forli-Cesena", "Emilia-Romagna")),
        Pair("FR", Pair("Frosinone", "Lazio")),
        Pair("GE", Pair("Genova", "Liguria")),
        Pair("GO", Pair("Gorizia", "Friuli-Venezia-Giulia")),
        Pair("GR", Pair("Grosseto", "Toscana")),
        Pair("IM", Pair("Imperia", "Liguria")),
        Pair("IS", Pair("Isernia", "Molise")),
        Pair("SP", Pair("La-Spezia", "Liguria")),
        Pair("LT", Pair("Latina", "Lazio")),
        Pair("LE", Pair("Lecce", "Puglia")),
        Pair("LC", Pair("Lecco", "Lombardia")),
        Pair("LI", Pair("Livorno", "Toscana")),
        Pair("LO", Pair("Lodi", "Lombardia")),
        Pair("LU", Pair("Lucca", "Toscana")),
        Pair("MC", Pair("Macerata", "Marche")),
        Pair("MN", Pair("Mantova", "Lombardia")),
        Pair("MS", Pair("Massa-Carrara", "Toscana")),
        Pair("MT", Pair("Matera", "Basilicata")),
        Pair("VS", Pair("Medio-Campidano", "Sardegna")),
        Pair("ME", Pair("Messina", "Sicilia")),
        Pair("MI", Pair("Milano", "Lombardia")),
        Pair("MO", Pair("Modena", "Emilia-Romagna")),
        Pair("MB", Pair("Monza-Brianza", "Lombardia")),
        Pair("NA", Pair("Napoli", "Campania")),
        Pair("NO", Pair("Novara", "Piemonte")),
        Pair("NU", Pair("Nuoro", "Sardegna")),
        Pair("OG", Pair("Ogliastra", "Sardegna")),
        Pair("OT", Pair("Olbia-Tempio", "Sardegna")),
        Pair("OR", Pair("Oristano", "Sardegna")),
        Pair("PD", Pair("Padova", "Veneto")),
        Pair("PA", Pair("Palermo", "Sicilia")),
        Pair("PR", Pair("Parma", "Emilia-Romagna")),
        Pair("PV", Pair("Pavia", "Lombardia")),
        Pair("PG", Pair("Perugia", "Umbria")),
        Pair("PU", Pair("Pesaro-Urbino", "Marche")),
        Pair("PE", Pair("Pescara", "Abruzzo")),
        Pair("PC", Pair("Piacenza", "Emilia-Romagna")),
        Pair("PI", Pair("Pisa", "Toscana")),
        Pair("PT", Pair("Pistoia", "Toscana")),
        Pair("PN", Pair("Pordenone", "Friuli-Venezia-Giulia")),
        Pair("PZ", Pair("Potenza", "Basilicata")),
        Pair("PO", Pair("Prato", "Toscana")),
        Pair("RG", Pair("Ragusa", "Sicilia")),
        Pair("RA", Pair("Ravenna", "Emilia-Romagna")),
        Pair("RC", Pair("Reggio-Calabria", "Calabria")),
        Pair("RE", Pair("Reggio-Emilia", "Emilia-Romagna")),
        Pair("RI", Pair("Rieti", "Lazio")),
        Pair("RN", Pair("Rimini", "Emilia-Romagna")),
        Pair("RM", Pair("Roma", "Lazio")),
        Pair("RO", Pair("Rovigo", "Veneto")),
        Pair("SA", Pair("Salerno", "Campania")),
        Pair("SS", Pair("Sassari", "Sardegna")),
        Pair("SV", Pair("Savona", "Liguria")),
        Pair("SI", Pair("Siena", "Toscana")),
        Pair("SR", Pair("Siracusa", "Sicilia")),
        Pair("SO", Pair("Sondrio", "Lombardia")),
        Pair("TA", Pair("Taranto", "Puglia")),
        Pair("TE", Pair("Teramo", "Abruzzo")),
        Pair("TR", Pair("Terni", "Umbria")),
        Pair("TO", Pair("Torino", "Piemonte")),
        Pair("TP", Pair("Trapani", "Sicilia")),
        Pair("TN", Pair("Trento", "Trentino-Alto-Adige")),
        Pair("TV", Pair("Treviso", "Veneto")),
        Pair("TS", Pair("Trieste", "Friuli-Venezia-Giulia")),
        Pair("UD", Pair("Udine", "Friuli-Venezia-Giulia")),
        Pair("VA", Pair("Varese", "Lombardia")),
        Pair("VE", Pair("Venezia", "Veneto")),
        Pair("VB", Pair("Verbania", "Piemonte")),
        Pair("VC", Pair("Vercelli", "Piemonte")),
        Pair("VR", Pair("Verona", "Veneto")),
        Pair("VV", Pair("Vibo-Valentia", "Calabria")),
        Pair("VI", Pair("Vicenza", "Veneto")),
        Pair("VT", Pair("Viterbo", "Lazio"))
)


