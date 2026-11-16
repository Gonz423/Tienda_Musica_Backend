package tienda_musica.producto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tienda_musica.producto.model.Producto;
import tienda_musica.producto.repository.ProductoRepository;

import java.util.List;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final ProductoRepository productoRepository;

    public DatosIniciales(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            List<Producto> lista = List.of(
                new Producto("GA001", "Guitarras Acústicas", "Guitarra Acústica Folk", "Yamaha", "F310", 8, 129990, "Tapa de abeto, aros y fondo de meranti. Ideal para iniciantes.", "GA001.jpg"),
                new Producto("GA002", "Guitarras Acústicas", "Guitarra Acústica Dreadnought", "Fender", "CD-60S", 5, 189990, "Tapa de abeto macizo, brazo de caoba. Sonido cálido y proyectado.", "GA002.jpg"),
                new Producto("GA003", "Guitarras Acústicas", "Guitarra Acústica Clásica 4/4", "Yamaha", "C40", 10, 89990, "Nailon, tapa de abeto. Ideal para estudio y flamenco.", "GA003.jpg"),
                new Producto("GA004", "Guitarras Acústicas", "Guitarra Electroacústica", "Takamine", "GN20CE", 3, 349990, "Pickup integrado, afinador incorporado.", "GA004.jpg"),
                new Producto("GA005", "Guitarras Acústicas", "Guitarra 3/4 Niños", "Yamaha", "JR1", 6, 79990, "Tamaño reducido para niños de 6 a 10 años.", "GA005.jpg"),
                new Producto("GE001", "Guitarras Eléctricas", "Guitarra Eléctrica Stratocaster", "Squier", "Affinity Strat", 5, 249990, "Cuerpo de álamo, mástil de arce, pastillas SSS.", "GE001.jpg"),
                new Producto("GE002", "Guitarras Eléctricas", "Guitarra Eléctrica Les Paul", "Epiphone", "Les Paul Std", 4, 329990, "Cuerpo caoba, tapa arce, pastillas humbucker.", "GE002.jpg"),
                new Producto("GE003", "Guitarras Eléctricas", "Guitarra Eléctrica SG", "Epiphone", "SG Standard", 3, 319990, "Cuerpo caoba, mástil caoba, 2 humbuckers.", "GE003.jpg"),
                new Producto("GE004", "Guitarras Eléctricas", "Guitarra Eléctrica Telecaster", "Squier", "Affinity Tele", 4, 239990, "Cuerpo álamo, clavijero vintage, 2 pastillas single.", "GE004.jpg"),
                new Producto("GE005", "Guitarras Eléctricas", "Guitarra Eléctrica Semi-hollow", "Epiphone", "ES-335", 2, 549990, "Semi-hueca, 2 humbuckers, ideal para jazz y blues.", "GE005.jpg"),
                new Producto("BA001", "Bajos Eléctricos", "Bajo Eléctrico 4 Cuerdas", "Squier", "Affinity PJ", 5, 299990, "Pickup PJ, cuerpo álamo, mástil arce.", "BA001.jpg"),
                new Producto("BA002", "Bajos Eléctricos", "Bajo Eléctrico Jazz Bass", "Fender", "Player Jazz", 2, 699990, "Alder body, 2 Alnico V Jazz single-coil.", "BA002.jpg"),
                new Producto("BA003", "Bajos Eléctricos", "Bajo Acústico 4 Cuerdas", "Yamaha", "APX700II", 2, 429990, "Electroacústico, afinador incorporado.", "BA003.jpg"),
                new Producto("BT001", "Baterías", "Batería Acústica 5 piezas", "Pearl", "Roadshow", 2, 599990, "Incluye stands, platillos y pedal de bombo.", "BT001.jpg"),
                new Producto("BT002", "Baterías", "Batería Electrónica 8 pads", "Roland", "TD-02KV", 2, 799990, "Módulo TD-02, 8 pads de goma, pedal hi-hat.", "BT002.jpg"),
                new Producto("BT003", "Baterías", "Caja Snare 14\"", "Pearl", "STE1450", 4, 89990, "Acero, 14x5\", 10 tensores.", "BT003.jpg"),
                new Producto("BT004", "Baterías", "Platillo Hi-Hat 14\"", "Zildjian", "A Series", 3, 149990, "Latón B20, sonido brillante y claro.", "BT004.jpg"),
                new Producto("BT005", "Baterías", "Platillo Crash 16\"", "Zildjian", "A Series", 3, 129990, "Latón B20, ataque rápido.", "BT005.jpg"),
                new Producto("TC001", "Teclados y Pianos", "Teclado Digital 61 teclas", "Yamaha", "PSR-E373", 4, 249990, "61 teclas sensibles al tacto, 622 voces.", "TC001.jpg"),
                new Producto("TC002", "Teclados y Pianos", "Piano Digital 88 teclas", "Yamaha", "P-45", 2, 499990, "88 teclas pesadas, 10 voces, pedal sustain incluido.", "TC002.jpg"),
                new Producto("TC003", "Teclados y Pianos", "Sintetizador 49 teclas", "Arturia", "MiniLab MKII", 5, 129990, "MIDI controller, 49 mini teclas.", "TC003.jpg"),
                new Producto("TC004", "Teclados y Pianos", "Teclado MIDI 88 teclas", "M-Audio", "Hammer 88", 2, 399990, "88 teclas martillo, sin sonidos propios.", "TC004.jpg"),
                new Producto("AM001", "Amplificadores", "Amplificador Guitarra 15W", "Fender", "Frontman 15G", 5, 99990, "15W, distorsión incorporada, entrada auxiliar.", "AM001.jpg"),
                new Producto("AM002", "Amplificadores", "Amplificador Guitarra 40W", "Marshall", "MG40GFX", 3, 299990, "40W, 4 canales, efectos digitales integrados.", "AM002.jpg"),
                new Producto("AM003", "Amplificadores", "Amplificador Bajo 100W", "Hartke", "HD100", 2, 449990, "100W, tweeter integrado, ecualizador de 4 bandas.", "AM003.jpg"),
                new Producto("AM004", "Amplificadores", "Amplificador Acústico 40W", "Fishman", "Loudbox Mini", 2, 499990, "60W, 2 canales, reverb y chorus incorporados.", "AM004.jpg"),
                new Producto("MI001", "Micrófonos", "Micrófono Dinámico Cardioide", "Shure", "SM58", 8, 149990, "Estándar industria para voz en vivo.", "MI001.jpg"),
                new Producto("MI002", "Micrófonos", "Micrófono Dinámico Instrumento", "Shure", "SM57", 6, 139990, "Ideal para captura de instrumentos y amplificadores.", "MI002.jpg"),
                new Producto("MI003", "Micrófonos", "Micrófono Condensador", "Audio-Tech.", "AT2020", 4, 199990, "Cardioide, XLR, ideal para grabación en estudio.", "MI003.jpg"),
                new Producto("MI004", "Micrófonos", "Micrófono USB de Condensador", "Blue", "Yeti", 5, 299990, "USB, 4 patrones polares, ideal para streaming y podcast.", "MI004.jpg"),
                new Producto("PE001", "Pedales de Efectos", "Pedal Distorsión", "Boss", "DS-1", 7, 79990, "Clásico pedal de distorsión, 3 controles.", "PE001.jpg"),
                new Producto("PE002", "Pedales de Efectos", "Pedal Reverb", "Boss", "RV-6", 4, 179990, "8 modos de reverb, control de shimmer.", "PE002.jpg"),
                new Producto("PE003", "Pedales de Efectos", "Pedal Multi-efectos", "Boss", "ME-80", 2, 349990, "Diseño tipo pedalboard, 8 efectos simultáneos.", "PE003.jpg"),
                new Producto("PE004", "Pedales de Efectos", "Pedal Tuner Cromático", "Boss", "TU-3", 8, 89990, "Afinador cromático, indicador de tono.", "PE004.jpg"),
                new Producto("PE005", "Pedales de Efectos", "Pedal Delay", "MXR", "Carbon Copy", 4, 179990, "Delay analógico cálido, tiempo 600ms.", "PE005.jpg"),
                new Producto("PE006", "Pedales de Efectos", "Pedal Overdrive", "Ibanez", "TS9", 6, 99990, "Tube Screamer clásico, sonido suave y orgánico.", "PE006.jpg"),
                new Producto("AC001", "Accesorios", "Cuerdas Guitarra Eléctrica 09-42", "Ernie Ball", "Super Slinky", 25, 8990, "Juego 6 cuerdas, calibre ligero.", "AC001.jpg"),
                new Producto("AC002", "Accesorios", "Cuerdas Guitarra Acústica 12-53", "Ernie Ball", "Earthwood", 20, 10990, "Bronce fósforo, sonido cálido.", "AC002.jpg"),
                new Producto("AC003", "Accesorios", "Cuerdas Bajo 45-105", "Ernie Ball", "Regular Slinky", 12, 14990, "Cuerdas de níquel enrollado, set 4 cuerdas.", "AC003.jpg"),
                new Producto("AC004", "Accesorios", "Púas de Guitarra x10 (0.73mm)", "Fender", "351", 50, 3990, "Celulosa, grosor medio.", "AC004.jpg"),
                new Producto("AC005", "Accesorios", "Capotraste Guitarra", "Dunlop", "Trigger", 15, 12990, "Capotraste de resorte, compatible 6 cuerdas.", "AC005.jpg"),
                new Producto("AC006", "Accesorios", "Afinador de Clip", "Snark", "SN-5", 20, 8990, "Afinador cromático de clip, pantalla giratoria.", "AC006.jpg"),
                new Producto("AC007", "Accesorios", "Cable Instrumento 3m", "Monster", "S100-I-3", 15, 12990, "Cable trenzado, conectores dorados, 3 metros.", "AC007.jpg"),
                new Producto("AC008", "Accesorios", "Cable Instrumento 6m", "Monster", "S100-I-6", 10, 17990, "Cable trenzado, conectores dorados, 6 metros.", "AC008.jpg"),
                new Producto("AC009", "Accesorios", "Soporte Guitarra de Piso", "Hercules", "GS302B", 12, 22990, "Soporte plegable con enganche automático.", "AC009.jpg"),
                new Producto("AC010", "Accesorios", "Soporte Guitarra de Pared", "Hercules", "WAH-202", 10, 18990, "Montaje a pared, enganche automático.", "AC010.jpg"),
                new Producto("ES001", "Estudio y Grabación", "Interfaz de Audio 2x2 USB", "Focusrite", "Scarlett Solo", 4, 149990, "1 entrada XLR+instrumento, 2 salidas, 24bit/192kHz.", "ES001.jpg"),
                new Producto("ES002", "Estudio y Grabación", "Auriculares de Estudio", "Audio-Tech.", "ATH-M20x", 6, 79990, "Circumaurales, respuesta 15Hz-20kHz.", "ES002.jpg"),
                new Producto("ES003", "Estudio y Grabación", "Auriculares de Estudio Pro", "Audio-Tech.", "ATH-M50x", 4, 219990, "Referencia de industria, sonido neutro y detallado.", "ES003.jpg"),
                new Producto("ES004", "Estudio y Grabación", "Monitor de Estudio 5\"", "Yamaha", "HS5", 2, 349990, "Altavoz activo, respuesta plana, ideal mezcla.", "ES004.jpg"),
                new Producto("ES005", "Estudio y Grabación", "Pop Filter para Micrófono", "Sennheiser", "MZP 40", 8, 14990, "Doble malla, brazo flexible con clip.", "ES005.jpg")
            );
            productoRepository.saveAll(lista);
        }
    }
}