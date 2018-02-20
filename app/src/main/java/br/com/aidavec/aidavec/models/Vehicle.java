package br.com.aidavec.aidavec.models;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Leonardo Saganski on 27/11/16.
 */
public class Vehicle {
    public int vei_id;
    public int usr_id;
    public String vei_marca;
    public String vei_modelo;
    public String vei_ano;
    public String vei_cor;
    public String vei_cobertura;
    public String photoA;
    public String photoB;
    public String photoC;
    public int vei_status;
    public int vei_okfotoa;
    public int vei_okfotob;
    public int vei_okfotoc;

    private static String[] Marcas = {"Marca", "CHERY","CHEVROLET","CITROËN", "FIAT","FORD","HONDA","HYUNDAI","JAC","KIA","PEUGEOUT","RENAULT","TOYOTA","VOLKSWAGEN","NISSAN"};

    private static String[][] Modelos = {
            {"Modelo","CELER","CIELO","FACE","NEW CELER","QQ","TIGGO"},
            {"Modelo","A10","A20","Agile","Astra","Astrovan","Bel Air","Blazer","Bonanza","Brasil","Brasinca","C10","C14","C20","Calibra Coupe","Camaro","Caprice","Captiva","Caravan","Cavalier","Celta","Chevette","Chevy 500","Cheyenne","Classic","Cobalt","Colorado","Corsa","Corsica Sedan","Corvette","Cruze","D10","D20","Equinox","HHR","Impala","Ipanema","Kadett","Lumina APV","Malibu","Marajo","Meriva","Montana","Monza","Omega","Onix","Opala","Pick Up Chevrolet","Prisma","S10","Silverado","Sonic","Space Van","Spin","SS10","SSR","Suburban","Suprema","Tigra","Tracker","Trafic","TrailBlazer","Vectra","Veraneio","Zafira"},
            {"Modelo","Aircross","AX","Berlingo","BX","C3","C3 Picasso","C4","C5","C6","C8","DS3","DS4","DS5","Evasion","Jumper","Xantia","XM","Xsara","ZX"},
            {"Modelo","147","500","Brava","Bravo","Cinquecento","Coupe","Doblò","Ducato","Duna","Elba","Fiorino","Fiorino Pick-Up","Freemont","Grand Siena","Idea","Linea","Marea","Mobi","Oggi","Palio","Palio Weekend","Panorama","Premio","Punto","Punto Cabrio","Siena","Stilo","Strada","Tempra","Tipo","Toro","Uno","Weekend"},
            {"Modelo","Aerostar","Aspire","Belina","Club Wagon","Contour","Corcel","Courier","Crown Victoria","Del Rey","Edge","Escort","Ecosport","Excursion","Expedition","Explorer","F-1","F-100","F-1000","F-150","F-250","F-350","F-4000","F75","Fairlane","Fiesta","Focus","Furglaine","Fusion","Galaxie","Ibiza","Jeep","Ka","Landau","Lincoln","Maverick","Mondeo","New Fiesta","Pampa","Probe","Ranger","Rural","Taurus","Thunderbird","Transit","Verona","Versailles","Windstar"},
            {"Modelo","Accord", "City","Civic", "CR-V","Fit","HR-V","Legend","NSX","Odyssey","Passport","Prelude"},
            {"Modelo","Azera","Accent","Atos","Coupe FX","Creta","Elantra","Elantra Wagon","Equus","Excel","Galloper","Genesis","H1","H100","HB20","HR","i30","ix35","Matrix","Porter","Santa Fe","Scoupe","Sonata","Terracan","Trajet","Tucson","Veloster","Veracruz"},
            {"Modelo","J2","J3","J5","J6","T5","T6","T140","T8"},
            {"Modelo","Bongo","Besta","Cadenza","Carens","Carnival","Cerato","Ceres","Clarus","Grand Carnival","Magentis","Mohave","Opirus","Optima","Picanto","Quoris","Rio","Sephia","Shuma","Sorento","Soul","Sportage"},
            {"Modelo","106","205","206","207","208","306","307","308","405","406","407","408","504","505","508","605","607","806","807","2008","3008","Boxer","Hoggar","Partner","RCZ"},
            {"Modelo","Clio","Duster","Fluence","Grand Scenic","Kangoo","Kangoo Express","Laguna","Logan","Master","Megane","Safrane","Sandero","Sandero Stepway","Scenic","Symbol","Trafic","Twingo"},
            {"Modelo","Avalon","Camry","Camry Wagon","Celica","Corolla","Corona Sedan","Etios Hatch","FJ Cruiser","Hilux","Land Cruiser Prado","MR2","Paseo","Previa","Prius","RAV4","Sequoia","Sienna","Supra","SW4","T100","Tacoma","Trip","Tundra"},
            {"Modelo","Amarok","Apollo","Bora","Brasília","Caravelle","CC","Corrado","CrossFox","Eos","Eurovan","Fox","Fusca","Gol","Golf","Jetta","Karmann Ghia","Kombi","Logus","New Beetle","Parati","Passat","Pointer","Polo","Quantum","Santana","Saveiro","Scirocco","SP2","SpaceCross","SpaceFox","Tiguan","TL","Touareg","Up!","Van","Variant","Voyage"},
            {"Modelo","350Z","370Z","Altima","Armada","AX","Frontier","Grand Livina","GT-R","Juke","Kicks","King Cab","Leaf","Livina","March","Maxima","Micra","Murano","NX 2000","Pathfinder","Pick Up D21","Pick Up D22","Primera","Quest","Sentra","Stanza","SX","Terrano II","Tiida","Versa","XTerra","XTRAIL","ZX"}
    };

    private static String[] Anos = {"Ano", "2005", "2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017"};

    private static String[] Cores = {"Cor", "Prata", "Preto","Cinza","Branco","Vermelho","Azul","Verde","Amarelo"};

    private static String[] Coberturas = {"Cobertura", "Básico", "Médio","Completo"};


    public int getVei_id() {
        return vei_id;
    }

    public void setVei_id(int vei_id) {
        this.vei_id = vei_id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public String getVei_marca() {
        return vei_marca;
    }

    public void setVei_marca(String vei_marca) {
        this.vei_marca = vei_marca;
    }

    public String getVei_modelo() {
        return vei_modelo;
    }

    public void setVei_modelo(String vei_modelo) {
        this.vei_modelo = vei_modelo;
    }

    public String getVei_ano() {
        return vei_ano;
    }

    public void setVei_ano(String vei_ano) {
        this.vei_ano = vei_ano;
    }

    public String getVei_cor() {
        return vei_cor;
    }

    public void setVei_cor(String vei_cor) {
        this.vei_cor = vei_cor;
    }

    public String getVei_cobertura() {
        return vei_cobertura;
    }

    public void setVei_cobertura(String vei_cobertura) {
        this.vei_cobertura = vei_cobertura;
    }

    public String getPhotoA() {
        return photoA;
    }

    public void setPhotoA(String photoA) {
        this.photoA = photoA;
    }

    public String getPhotoB() {
        return photoB;
    }

    public void setPhotoB(String photoB) {
        this.photoB = photoB;
    }

    public String getPhotoC() {
        return photoC;
    }

    public void setPhotoC(String photoC) {
        this.photoC = photoC;
    }

    public static List<String> getMarcasList(){
        return Arrays.asList(Marcas);
    }

    public static List<String> getAnosList(){
        return Arrays.asList(Anos);
    }

    public static List<String> getCoresList(){
        return Arrays.asList(Cores);
    }

    public static List<String> getCoberturasList(){
        return Arrays.asList(Coberturas);
    }

    public static List<String> getModelosbyMarca(int id){
        return Arrays.asList(Modelos[id]);
    }

    public int getVei_status() {
        return vei_status;
    }

    public void setVei_status(int vei_status) {
        this.vei_status = vei_status;
    }

    public int getVei_okfotoa() {
        return vei_okfotoa;
    }

    public void setVei_okfotoa(int vei_okfotoa) {
        this.vei_okfotoa = vei_okfotoa;
    }

    public int getVei_okfotob() {
        return vei_okfotob;
    }

    public void setVei_okfotob(int vei_okfotob) {
        this.vei_okfotob = vei_okfotob;
    }

    public int getVei_okfotoc() {
        return vei_okfotoc;
    }

    public void setVei_okfotoc(int vei_okfotoc) {
        this.vei_okfotoc = vei_okfotoc;
    }
}
