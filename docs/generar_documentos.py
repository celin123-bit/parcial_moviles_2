# -*- coding: utf-8 -*-
"""Genera la presentación y el manual de usuario de Kusi Tours."""
from pathlib import Path

from docx import Document
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_LINE_SPACING
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
from docx.shared import Cm, Inches, Pt, RGBColor
from pptx import Presentation
from pptx.dml.color import RGBColor as PptColor
from pptx.enum.shapes import MSO_SHAPE
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.util import Emu, Inches as PptInches, Pt as PptPt

OUT = Path(__file__).resolve().parent
PRIMARY = PptColor(0xA8, 0x44, 0x2B)
PRIMARY_DARK = PptColor(0x6D, 0x27, 0x1B)
ACCENT = PptColor(0xF4, 0xC4, 0x7A)
BG = PptColor(0xFF, 0xF8, 0xF4)
SURFACE = PptColor(0xFC, 0xE9, 0xD8)
INK = PptColor(0x31, 0x1A, 0x14)
MUTED = PptColor(0x70, 0x57, 0x4D)
WHITE = PptColor(0xFF, 0xFF, 0xFF)
TEAL = PptColor(0x14, 0x6C, 0x60)

DOC_PRIMARY = RGBColor(0xA8, 0x44, 0x2B)
DOC_DARK = RGBColor(0x6D, 0x27, 0x1B)
DOC_INK = RGBColor(0x31, 0x1A, 0x14)
DOC_MUTED = RGBColor(0x70, 0x57, 0x4D)


def set_run(run, size=18, bold=False, color=INK, font="Calibri"):
    run.font.size = PptPt(size)
    run.font.bold = bold
    run.font.color.rgb = color
    run.font.name = font


def add_rect(slide, l, t, w, h, fill):
    shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, l, t, w, h)
    shape.fill.solid()
    shape.fill.fore_color.rgb = fill
    shape.line.fill.background()
    return shape


def add_round(slide, l, t, w, h, fill):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, l, t, w, h)
    shape.fill.solid()
    shape.fill.fore_color.rgb = fill
    shape.line.fill.background()
    shape.adjustments[0] = 0.08
    return shape


def add_text(slide, l, t, w, h, text, size=18, bold=False, color=INK, align=PP_ALIGN.LEFT, anchor=MSO_ANCHOR.TOP):
    box = slide.shapes.add_textbox(l, t, w, h)
    tf = box.text_frame
    tf.word_wrap = True
    tf.auto_size = None
    try:
        tf._txBody.bodyPr.set("anchor", {MSO_ANCHOR.TOP: "t", MSO_ANCHOR.MIDDLE: "ctr", MSO_ANCHOR.BOTTOM: "b"}.get(anchor, "t"))
    except Exception:
        pass
    p = tf.paragraphs[0]
    p.alignment = align
    run = p.add_run()
    run.text = text
    set_run(run, size=size, bold=bold, color=color)
    return box


def add_bullets(slide, l, t, w, h, items, size=16, color=INK, spacing=10):
    box = slide.shapes.add_textbox(l, t, w, h)
    tf = box.text_frame
    tf.word_wrap = True
    for i, item in enumerate(items):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.alignment = PP_ALIGN.LEFT
        p.space_after = PptPt(spacing)
        p.level = 0
        run = p.add_run()
        run.text = "•  " + item
        set_run(run, size=size, color=color)
    return box


def blank(prs):
    slide = prs.slides.add_slide(prs.slide_layouts[6])
    add_rect(slide, 0, 0, prs.slide_width, prs.slide_height, BG)
    return slide


def footer(slide, page, total=14):
    add_rect(slide, 0, PptInches(7.15), PptInches(13.333), PptInches(0.35), PRIMARY_DARK)
    add_text(slide, PptInches(0.4), PptInches(7.16), PptInches(8), PptInches(0.32),
             "Kusi Tours  ·  Manual de presentación del aplicativo", size=11, color=ACCENT)
    add_text(slide, PptInches(11.4), PptInches(7.16), PptInches(1.5), PptInches(0.32),
             f"{page} / {total}", size=11, color=WHITE, align=PP_ALIGN.RIGHT)


def header_bar(slide, kicker, title):
    add_rect(slide, 0, 0, PptInches(13.333), PptInches(1.25), PRIMARY)
    add_rect(slide, 0, PptInches(1.25), PptInches(13.333), PptInches(0.08), ACCENT)
    add_text(slide, PptInches(0.5), PptInches(0.12), PptInches(12), PptInches(0.32),
             kicker.upper(), size=12, bold=True, color=ACCENT)
    add_text(slide, PptInches(0.5), PptInches(0.42), PptInches(12), PptInches(0.7),
             title, size=28, bold=True, color=WHITE)


def card(slide, l, t, w, h, title, body, accent=PRIMARY):
    add_round(slide, l, t, w, h, WHITE)
    add_rect(slide, l, t, PptInches(0.12), h, accent)
    add_text(slide, l + PptInches(0.28), t + PptInches(0.12), w - PptInches(0.4), PptInches(0.4),
             title, size=16, bold=True, color=PRIMARY)
    add_text(slide, l + PptInches(0.28), t + PptInches(0.5), w - PptInches(0.4), h - PptInches(0.62),
             body, size=13, color=INK)


def build_pptx():
    prs = Presentation()
    prs.slide_width = PptInches(13.333)
    prs.slide_height = PptInches(7.5)
    total = 14

    # 1 Portada
    s = blank(prs)
    add_rect(s, 0, 0, PptInches(13.333), PptInches(7.5), PRIMARY_DARK)
    add_rect(s, 0, 0, PptInches(0.28), PptInches(7.5), ACCENT)
    add_text(s, PptInches(0.8), PptInches(1.5), PptInches(11), PptInches(0.4),
             "KUSI TOURS  /  PERÚ", size=16, bold=True, color=ACCENT)
    add_text(s, PptInches(0.8), PptInches(2.0), PptInches(12), PptInches(1.3),
             "Aplicativo móvil de reservas turísticas", size=40, bold=True, color=WHITE)
    add_text(s, PptInches(0.8), PptInches(3.4), PptInches(11), PptInches(0.7),
             "Viajes cercanos. Historias que quedan.", size=22, color=SURFACE)
    add_text(s, PptInches(0.8), PptInches(4.4), PptInches(11), PptInches(0.8),
             "Presentación del producto  ·  Android nativo  ·  Proyecto académico", size=16, color=ACCENT)
    add_text(s, PptInches(0.8), PptInches(6.4), PptInches(11), PptInches(0.4),
             "Paquete: pe.edu.moivo   ·   Nombre en pantalla: Kusi Tours", size=13, color=WHITE)

    # 2 Agenda
    s = blank(prs)
    header_bar(s, "Contenido", "Agenda")
    items = [
        ("01", "Qué es Kusi Tours y a quién sirve"),
        ("02", "Módulos: paquetes, clientes, reservas y mapa"),
        ("03", "Flujo completo de una reserva"),
        ("04", "Catálogo de experiencias en Perú"),
        ("05", "Estados, validaciones y persistencia local"),
        ("06", "Alcance académico y cierre"),
    ]
    for i, (num, txt) in enumerate(items):
        col = i % 2
        row = i // 2
        l = PptInches(0.5 + col * 6.4)
        t = PptInches(1.7 + row * 1.6)
        add_round(s, l, t, PptInches(6.0), PptInches(1.35), WHITE)
        add_text(s, l + PptInches(0.25), t + PptInches(0.25), PptInches(1.2), PptInches(0.8),
                 num, size=28, bold=True, color=PRIMARY)
        add_text(s, l + PptInches(1.5), t + PptInches(0.4), PptInches(4.2), PptInches(0.7),
                 txt, size=16, color=INK)
    footer(s, 2, total)

    # 3 Qué es
    s = blank(prs)
    header_bar(s, "Producto", "Qué es Kusi Tours")
    add_text(s, PptInches(0.5), PptInches(1.55), PptInches(12.3), PptInches(0.7),
             "Aplicación Android para organizar experiencias locales: consultar paquetes, registrar viajeros, crear reservas y ubicar el punto de encuentro.",
             size=18, color=INK)
    card(s, PptInches(0.5), PptInches(2.4), PptInches(4.0), PptInches(4.2),
         "Para quién",
         "Estudiantes, docentes y operadores de demostración que necesitan un recorrido completo de reserva turística en un teléfono Android, sin servidor ni pagos reales.")
    card(s, PptInches(4.7), PptInches(2.4), PptInches(4.0), PptInches(4.2),
         "Qué resuelve",
         "Centraliza catálogo, cliente y reserva en un solo lugar. Calcula el total, guarda el estado del viaje y muestra el encuentro en mapa (o en Google Maps externo).")
    card(s, PptInches(8.9), PptInches(2.4), PptInches(4.0), PptInches(4.2),
         "Cómo funciona",
         "Todo se guarda en el dispositivo (Room). La sesión demo es local. Los paquetes, horarios y precios vienen precargados y son de demostración académica.")
    footer(s, 3, total)

    # 4 Objetivos
    s = blank(prs)
    header_bar(s, "Propuesta", "Objetivos del aplicativo")
    goals = [
        ("Consultar", "Mostrar tres paquetes turísticos con destino, horario, precio, inclusiones y encuentro."),
        ("Registrar", "Dar de alta clientes con DNI único, correo y teléfono válidos."),
        ("Reservar", "Vincular cliente + paquete + fecha + personas, calcular total y guardar en PENDIENTE."),
        ("Acompañar", "Avanzar el estado a CONFIRMADO y FINALIZADO, y abrir el punto de encuentro."),
    ]
    for i, (title, body) in enumerate(goals):
        t = PptInches(1.6 + i * 1.3)
        add_round(s, PptInches(0.5), t, PptInches(12.3), PptInches(1.15), WHITE)
        add_rect(s, PptInches(0.5), t, PptInches(0.18), PptInches(1.15), ACCENT if i % 2 == 0 else TEAL)
        add_text(s, PptInches(1.0), t + PptInches(0.15), PptInches(11.4), PptInches(0.4), title, size=20, bold=True, color=PRIMARY)
        add_text(s, PptInches(1.0), t + PptInches(0.55), PptInches(11.4), PptInches(0.45), body, size=15, color=INK)
    footer(s, 4, total)

    # 5 Módulos
    s = blank(prs)
    header_bar(s, "Navegación", "Módulos de la aplicación")
    mods = [
        ("Inicio de sesión", "Acceso demo con correo y contraseña. Valida formato y credenciales. La sesión queda en el dispositivo."),
        ("Paquetes", "Catálogo en lista. Cada tarjeta abre el detalle: foto, precio, descripción, inclusiones y mapa."),
        ("Clientes", "Alta de viajero (nombre, DNI, correo, teléfono). El DNI no se puede repetir."),
        ("Reservas", "Formulario de viaje, listado y detalle. Total = tarifa × personas. Estados del ciclo de vida."),
        ("Mapa", "Marcador del encuentro. Opcional: mi ubicación. Alternativa: abrir en Google Maps."),
        ("Menú", "Cerrar sesión sin borrar clientes ni reservas. Los datos solo se pierden al desinstalar o borrar datos de Android."),
    ]
    for i, (title, body) in enumerate(mods):
        col = i % 3
        row = i // 3
        l = PptInches(0.4 + col * 4.3)
        t = PptInches(1.6 + row * 2.55)
        add_round(s, l, t, PptInches(4.05), PptInches(2.35), WHITE)
        add_text(s, l + PptInches(0.22), t + PptInches(0.2), PptInches(3.6), PptInches(0.5), title, size=18, bold=True, color=PRIMARY)
        add_text(s, l + PptInches(0.22), t + PptInches(0.75), PptInches(3.6), PptInches(1.4), body, size=14, color=INK)
    footer(s, 5, total)

    # 6 Acceso
    s = blank(prs)
    header_bar(s, "Primer uso", "Acceso de demostración")
    add_round(s, PptInches(0.5), PptInches(1.6), PptInches(6.2), PptInches(5.1), WHITE)
    add_text(s, PptInches(0.8), PptInches(1.85), PptInches(5.6), PptInches(0.4), "Credenciales", size=20, bold=True, color=PRIMARY)
    add_text(s, PptInches(0.8), PptInches(2.4), PptInches(5.6), PptInches(0.9),
             "Correo\ndemo@kusitours.pe", size=18, color=INK)
    add_text(s, PptInches(0.8), PptInches(3.5), PptInches(5.6), PptInches(0.9),
             "Contraseña\nKusi2026!", size=18, color=INK)
    add_text(s, PptInches(0.8), PptInches(4.7), PptInches(5.6), PptInches(1.6),
             "El correo no distingue mayúsculas y se recortan espacios. La contraseña sí distingue mayúsculas. No hay servidor: no se envían credenciales.",
             size=14, color=MUTED)
    add_round(s, PptInches(7.0), PptInches(1.6), PptInches(5.8), PptInches(5.1), WHITE)
    add_text(s, PptInches(7.3), PptInches(1.85), PptInches(5.3), PptInches(0.4), "Validaciones al ingresar", size=20, bold=True, color=PRIMARY)
    add_bullets(s, PptInches(7.3), PptInches(2.5), PptInches(5.3), PptInches(3.8), [
        "Campos vacíos: pide correo y contraseña.",
        "Correo inválido: exige un formato con @ y dominio.",
        "Datos incorrectos: no abre el catálogo.",
        "Si ya hay sesión, salta directo al inicio.",
        "Es acceso académico, no autenticación de producción.",
    ], size=15)
    footer(s, 6, total)

    # 7 Flujo
    s = blank(prs)
    header_bar(s, "Recorrido", "Flujo de una reserva")
    steps = [
        ("1", "Ingresar", "Credenciales demo"),
        ("2", "Elegir paquete", "Abrir detalle"),
        ("3", "Registrar cliente", "DNI único"),
        ("4", "Completar reserva", "Fecha y personas"),
        ("5", "Confirmar", "PENDIENTE → CONFIRMADO"),
        ("6", "Finalizar", "Encuentro en mapa"),
    ]
    for i, (n, t1, t2) in enumerate(steps):
        l = PptInches(0.35 + i * 2.16)
        add_round(s, l, PptInches(1.7), PptInches(2.0), PptInches(3.3), WHITE)
        circle = s.shapes.add_shape(MSO_SHAPE.OVAL, l + PptInches(0.6), PptInches(1.95), PptInches(0.75), PptInches(0.75))
        circle.fill.solid()
        circle.fill.fore_color.rgb = PRIMARY
        circle.line.fill.background()
        tf = circle.text_frame
        tf.word_wrap = False
        tf.paragraphs[0].alignment = PP_ALIGN.CENTER
        try:
            tf._txBody.bodyPr.set("anchor", "ctr")
        except Exception:
            pass
        run = tf.paragraphs[0].add_run()
        run.text = n
        set_run(run, size=18, bold=True, color=WHITE)
        add_text(s, l + PptInches(0.08), PptInches(2.9), PptInches(1.84), PptInches(0.9), t1, size=15, bold=True, color=PRIMARY, align=PP_ALIGN.CENTER)
        add_text(s, l + PptInches(0.08), PptInches(3.8), PptInches(1.84), PptInches(0.9), t2, size=13, color=MUTED, align=PP_ALIGN.CENTER)
        if i < 5:
            add_text(s, l + PptInches(1.85), PptInches(2.05), PptInches(0.35), PptInches(0.5), "→", size=20, bold=True, color=ACCENT)
    add_text(s, PptInches(0.5), PptInches(5.3), PptInches(12.3), PptInches(1.5),
             "Al guardar, la reserva aparece de inmediato en el listado con estado PENDIENTE. El total se calcula en pantalla (tarifa por persona × cantidad). La fecha no puede ser anterior a hoy y se aceptan de 1 a 30 personas.",
             size=16, color=INK)
    footer(s, 7, total)

    # 8 Catálogo
    s = blank(prs)
    header_bar(s, "Experiencias", "Catálogo de paquetes")
    packs = [
        ("Cusco esencial", "Cusco  ·  1 día  08:00–17:00", "S/ 180.00 / persona",
         "Centro histórico y Sacsayhuamán con guía local. Encuentro: Plaza de Armas de Cusco, junto a la fuente."),
        ("Paracas y Huacachina", "Ica  ·  1 día  06:00–20:00", "S/ 240.00 / persona",
         "Costa, desierto y oasis. Encuentro: Plaza de Armas de Ica, frente a la municipalidad."),
        ("Lima histórica", "Lima  ·  Medio día  09:00–13:00", "S/ 95.00 / persona",
         "Visita guiada a pie por el centro. Encuentro: Plaza San Martín, monumento central."),
    ]
    for i, (name, meta, price, desc) in enumerate(packs):
        l = PptInches(0.4 + i * 4.3)
        add_round(s, l, PptInches(1.6), PptInches(4.05), PptInches(5.15), WHITE)
        add_rect(s, l, PptInches(1.6), PptInches(4.05), PptInches(1.15), PRIMARY)
        add_text(s, l + PptInches(0.2), PptInches(1.75), PptInches(3.65), PptInches(0.8), name, size=20, bold=True, color=WHITE)
        add_text(s, l + PptInches(0.2), PptInches(2.95), PptInches(3.65), PptInches(0.7), meta, size=14, color=MUTED)
        add_text(s, l + PptInches(0.2), PptInches(3.6), PptInches(3.65), PptInches(0.5), price, size=20, bold=True, color=PRIMARY)
        add_text(s, l + PptInches(0.2), PptInches(4.3), PptInches(3.65), PptInches(2.1), desc, size=14, color=INK)
    footer(s, 8, total)

    # 9 Clientes y reservas
    s = blank(prs)
    header_bar(s, "Datos", "Clientes y reservas")
    card(s, PptInches(0.45), PptInches(1.55), PptInches(6.1), PptInches(5.2),
         "Cliente",
         "Campos: nombre (3–80 caracteres), DNI de 8 dígitos, correo válido y teléfono de 9 dígitos.\n\n"
         "El DNI es único: si se repite, el segundo registro se rechaza y el primero se conserva.\n\n"
         "Desde la lista se pueden ver los datos y crear una reserva con ese viajero ya seleccionado.\n\n"
         "También se puede registrar un cliente en medio del formulario de reserva.")
    card(s, PptInches(6.8), PptInches(1.55), PptInches(6.1), PptInches(5.2),
         "Reserva",
         "Obligatorio: cliente, paquete, fecha de viaje y cantidad de personas (1–30).\n\n"
         "El calendario bloquea fechas pasadas. El total se actualiza al cambiar paquete o personas.\n\n"
         "Cada reserva guarda una copia de la tarifa original. El dinero se almacena en céntimos.\n\n"
         "Relación: un cliente puede tener varias reservas; un paquete también.")
    footer(s, 9, total)

    # 10 Estados
    s = blank(prs)
    header_bar(s, "Ciclo de vida", "Estados de la reserva")
    states = [
        ("PENDIENTE", PRIMARY, "Se crea al guardar. Aún no está confirmada. Botón: Confirmar reserva."),
        ("CONFIRMADO", TEAL, "El viaje quedó aceptado. Botón: Finalizar reserva."),
        ("FINALIZADO", PRIMARY_DARK, "Estado terminal. Ya no hay otra transición. El mapa sigue disponible."),
    ]
    for i, (name, color, body) in enumerate(states):
        l = PptInches(0.45 + i * 4.25)
        add_round(s, l, PptInches(1.6), PptInches(4.0), PptInches(3.4), WHITE)
        add_rect(s, l, PptInches(1.6), PptInches(4.0), PptInches(1.1), color)
        add_text(s, l + PptInches(0.2), PptInches(1.85), PptInches(3.6), PptInches(0.7), name, size=22, bold=True, color=WHITE, align=PP_ALIGN.CENTER)
        add_text(s, l + PptInches(0.25), PptInches(2.95), PptInches(3.5), PptInches(1.8), body, size=16, color=INK)
        if i < 2:
            add_text(s, l + PptInches(3.85), PptInches(2.85), PptInches(0.5), PptInches(0.5), "→", size=22, bold=True, color=ACCENT)
    add_text(s, PptInches(0.5), PptInches(5.25), PptInches(12.3), PptInches(1.5),
             "El cambio pide confirmación. No se puede saltar de PENDIENTE a FINALIZADO ni revertir un estado. En el detalle se ven cliente, paquete, fecha, personas, tarifa, total, encuentro e inclusiones.",
             size=16, color=INK)
    footer(s, 10, total)

    # 11 Mapa
    s = blank(prs)
    header_bar(s, "Ubicación", "Punto de encuentro")
    add_bullets(s, PptInches(0.5), PptInches(1.6), PptInches(12.3), PptInches(2.4), [
        "El marcador del encuentro no requiere permiso de ubicación.",
        "Con API key de Maps: mapa integrado de Google y marcador del punto.",
        "Sin API key: la pantalla explica la configuración y se puede abrir el punto en Google Maps externo.",
        "«Mostrar mi ubicación» pide permiso (precisa o aproximada). Denegar no impide reservar ni ver el encuentro.",
    ], size=16)
    facts = [
        ("Centrar encuentro", "Vuelve la cámara al marcador del paquete."),
        ("Abrir en Google Maps", "Lanza la app o el navegador con las coordenadas."),
        ("Ajustes de ubicación", "Sirve si el GPS está apagado o el permiso está bloqueado."),
        ("Sin Internet", "Clientes y reservas siguen funcionando; el mapa puede no cargar."),
    ]
    for i, (t, b) in enumerate(facts):
        col = i % 2
        row = i // 2
        l = PptInches(0.5 + col * 6.4)
        top = PptInches(4.15 + row * 1.35)
        add_round(s, l, top, PptInches(6.1), PptInches(1.2), WHITE)
        add_text(s, l + PptInches(0.25), top + PptInches(0.12), PptInches(5.6), PptInches(0.4), t, size=16, bold=True, color=PRIMARY)
        add_text(s, l + PptInches(0.25), top + PptInches(0.52), PptInches(5.6), PptInches(0.5), b, size=13, color=INK)
    footer(s, 11, total)

    # 12 Tecnología
    s = blank(prs)
    header_bar(s, "Construcción", "Tecnología y persistencia")
    tech = [
        ("Android nativo", "Java 17 + XML. Mínimo Android 8.0 (API 26). Compila con SDK 35."),
        ("Pantallas", "Activities, View Binding, RecyclerView y barra inferior de tres secciones."),
        ("Datos", "Room. Relación Cliente 1→N Reserva N←1 Paquete. Sin servidor ni sincronización."),
        ("Mapas", "Google Maps SDK for Android. Permiso de ubicación solo para «Mostrar mi ubicación»."),
    ]
    for i, (t, b) in enumerate(tech):
        col = i % 2
        row = i // 2
        card(s, PptInches(0.45 + col * 6.45), PptInches(1.55 + row * 2.5), PptInches(6.2), PptInches(2.3), t, b, ACCENT if i % 2 else TEAL)
    footer(s, 12, total)

    # 13 Alcance
    s = blank(prs)
    header_bar(s, "Importante", "Alcance académico")
    add_round(s, PptInches(0.5), PptInches(1.6), PptInches(12.3), PptInches(5.15), WHITE)
    add_bullets(s, PptInches(0.8), PptInches(1.85), PptInches(11.7), PptInches(4.6), [
        "No hay compras, cobros reales ni cupos de operadores turísticos.",
        "Paquetes, horarios y precios son datos de demostración precargados.",
        "La sesión no es autenticación segura para producción.",
        "Los datos viven en el mismo dispositivo. Desinstalar o borrar datos de Android los elimina.",
        "Cerrar sesión conserva clientes y reservas.",
        "El mapa integrado necesita una API key propia de Google Cloud (Maps SDK for Android).",
        "La interfaz está en español. Requisito: emulador o teléfono con Google Play Services para el mapa.",
    ], size=17, spacing=8)
    footer(s, 13, total)

    # 14 Cierre
    s = blank(prs)
    add_rect(s, 0, 0, PptInches(13.333), PptInches(7.5), PRIMARY)
    add_rect(s, 0, 0, PptInches(0.28), PptInches(7.5), ACCENT)
    add_text(s, PptInches(0.8), PptInches(2.0), PptInches(12), PptInches(0.4),
             "KUSI TOURS  /  PERÚ", size=16, bold=True, color=ACCENT)
    add_text(s, PptInches(0.8), PptInches(2.5), PptInches(12), PptInches(1.2),
             "Listo para el recorrido de demostración", size=36, bold=True, color=WHITE)
    add_text(s, PptInches(0.8), PptInches(3.9), PptInches(11.5), PptInches(1.2),
             "Ingresa con demo@kusitours.pe  ·  Kusi2026!\nConsulta un paquete, registra un viajero, guarda la reserva y abre el punto de encuentro.",
             size=18, color=SURFACE)
    add_text(s, PptInches(0.8), PptInches(5.6), PptInches(11.5), PptInches(0.8),
             "Documentos: Presentación PPT y Manual de usuario en la carpeta docs/",
             size=14, color=ACCENT)

    path = OUT / "Presentacion_Kusi_Tours.pptx"
    prs.save(path)
    return path


def set_cell_shading(cell, hex_color):
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), hex_color)
    shd.set(qn("w:val"), "clear")
    tcPr.append(shd)


def shade_header_row(row, hex_color="6D271B"):
    for cell in row.cells:
        set_cell_shading(cell, hex_color)
        for p in cell.paragraphs:
            for run in p.runs:
                run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
                run.bold = True


def add_heading(doc, text, level=1):
    p = doc.add_heading(text, level=level)
    for run in p.runs:
        run.font.color.rgb = DOC_PRIMARY if level == 1 else DOC_DARK
        run.font.name = "Calibri"
    return p


def add_p(doc, text, bold=False, size=11, color=DOC_INK):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(text)
    run.bold = bold
    run.font.size = Pt(size)
    run.font.color.rgb = color
    run.font.name = "Calibri"
    return p


def add_li(doc, text):
    p = doc.add_paragraph(text, style="List Bullet")
    p.paragraph_format.space_after = Pt(4)
    for run in p.runs:
        run.font.name = "Calibri"
        run.font.size = Pt(11)
        run.font.color.rgb = DOC_INK
    return p


def set_narrow_margins(section):
    section.top_margin = Cm(2)
    section.bottom_margin = Cm(2)
    section.left_margin = Cm(2.2)
    section.right_margin = Cm(2.2)


def add_page_number(section):
    footer = section.footer
    footer.is_linked_to_previous = False
    p = footer.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    run = p.add_run("Kusi Tours  ·  Manual de usuario  ·  ")
    run.font.size = Pt(9)
    run.font.color.rgb = DOC_MUTED
    run.font.name = "Calibri"
    fld1 = OxmlElement("w:fldChar")
    fld1.set(qn("w:fldCharType"), "begin")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = " PAGE "
    fld2 = OxmlElement("w:fldChar")
    fld2.set(qn("w:fldCharType"), "end")
    r2 = p.add_run()
    r2._r.append(fld1)
    r2._r.append(instr)
    r2._r.append(fld2)
    r2.font.size = Pt(9)
    r2.font.color.rgb = DOC_MUTED


def table(doc, headers, rows):
    t = doc.add_table(rows=1 + len(rows), cols=len(headers))
    t.style = "Table Grid"
    t.autofit = True
    hdr = t.rows[0]
    for i, h in enumerate(headers):
        hdr.cells[i].text = h
    shade_header_row(hdr)
    for r_i, row in enumerate(rows):
        for c_i, val in enumerate(row):
            cell = t.rows[r_i + 1].cells[c_i]
            cell.text = val
            for p in cell.paragraphs:
                for run in p.runs:
                    run.font.size = Pt(10)
                    run.font.name = "Calibri"
                    run.font.color.rgb = DOC_INK
            if r_i % 2 == 1:
                set_cell_shading(cell, "FCE9D8")
    doc.add_paragraph()
    return t


def build_docx():
    doc = Document()
    section = doc.sections[0]
    set_narrow_margins(section)
    add_page_number(section)

    style = doc.styles["Normal"]
    style.font.name = "Calibri"
    style.font.size = Pt(11)
    style.font.color.rgb = DOC_INK

    title = doc.add_paragraph()
    title.alignment = WD_ALIGN_PARAGRAPH.LEFT
    r = title.add_run("KUSI TOURS / PERÚ")
    r.bold = True
    r.font.size = Pt(12)
    r.font.color.rgb = DOC_PRIMARY
    r.font.name = "Calibri"

    h = doc.add_paragraph()
    r = h.add_run("Manual de usuario")
    r.bold = True
    r.font.size = Pt(28)
    r.font.color.rgb = DOC_DARK
    r.font.name = "Calibri"

    add_p(doc, "Aplicativo móvil de reservas turísticas para Android.", size=14, color=DOC_MUTED)
    add_p(doc, "Versión de demostración académica  ·  Nombre en pantalla: Kusi Tours  ·  Paquete: pe.edu.moivo")
    add_p(doc, "Este manual explica, paso a paso, cómo entrar a la aplicación, consultar paquetes, registrar clientes, crear reservas, cambiar estados y ver el punto de encuentro. Está escrito para quien va a usar el teléfono, no para instalar el proyecto en Android Studio.")

    add_heading(doc, "1. Qué puedes hacer con la aplicación", 1)
    add_p(doc, "Kusi Tours organiza experiencias locales en un solo lugar. Desde el teléfono puedes:")
    add_li(doc, "Ver tres paquetes turísticos (Cusco, Ica y Lima) con precio, horario, inclusiones y encuentro.")
    add_li(doc, "Registrar viajeros con nombre, DNI, correo y teléfono.")
    add_li(doc, "Crear una reserva (cliente + paquete + fecha + personas) y ver el total en soles.")
    add_li(doc, "Confirmar y finalizar una reserva.")
    add_li(doc, "Abrir el punto de encuentro en el mapa o en Google Maps.")
    add_p(doc, "Los datos se guardan solo en este dispositivo. No hay compras ni cobros reales. Los paquetes y precios son de demostración.")

    add_heading(doc, "2. Requisitos para usarla", 1)
    add_li(doc, "Teléfono o emulador Android 8.0 o superior.")
    add_li(doc, "La aplicación instalada (APK de depuración del proyecto).")
    add_li(doc, "Para el mapa integrado: Internet, Google Play Services y una API key de Maps configurada al compilar.")
    add_li(doc, "Si no hay API key, igual puedes reservar y abrir el punto en Google Maps externo.")

    add_heading(doc, "3. Iniciar sesión", 1)
    add_p(doc, "Al abrir la app aparece la pantalla de acceso. El acceso es de demostración: no es un inicio de sesión de producción y no se envían datos a un servidor.")
    add_heading(doc, "Credenciales de demostración", 2)
    table(doc, ["Campo", "Valor"], [
        ["Correo electrónico", "demo@kusitours.pe"],
        ["Contraseña", "Kusi2026!"],
    ])
    add_p(doc, "El correo no distingue mayúsculas y se recortan espacios al inicio o al final. La contraseña sí distingue mayúsculas.")
    add_heading(doc, "Pasos", 2)
    add_li(doc, "Escribe el correo y la contraseña.")
    add_li(doc, "Pulsa Ingresar (también puedes confirmar desde el teclado de la contraseña).")
    add_li(doc, "Si los datos son correctos, se abre el catálogo de paquetes.")
    add_heading(doc, "Si algo falla", 2)
    table(doc, ["Qué ves", "Qué significa", "Qué hacer"], [
        ["Ingresa un correo válido.", "El correo está vacío o no tiene formato.", "Usa un correo con @ y dominio, por ejemplo el de demostración."],
        ["Ingresa la contraseña.", "El campo de contraseña está vacío.", "Escribe Kusi2026! respetando mayúsculas."],
        ["Credenciales incorrectas…", "Correo o contraseña no coinciden con la demo.", "Copia exactamente los datos de la tabla anterior."],
    ])
    add_p(doc, "Si ya habías ingresado y no cerraste sesión, la app puede abrir directo el inicio.")

    add_heading(doc, "4. Pantalla principal", 1)
    add_p(doc, "Después del login ves Kusi Tours con tres secciones en la barra inferior:")
    table(doc, ["Sección", "Qué muestra", "Botón inferior"], [
        ["Paquetes", "Los tres tours precargados, con destino, duración y precio por persona.", "Nueva reserva"],
        ["Reservas", "Tus reservas con estado, viajero, fecha, personas y total.", "Nueva reserva"],
        ["Clientes", "Viajeros registrados (DNI, correo y teléfono).", "Registrar cliente"],
    ])
    add_p(doc, "Toca una tarjeta para abrir el detalle. En Paquetes se abre el paquete; en Reservas, la reserva; en Clientes, un recuadro con los datos y la opción Crear reserva.")
    add_p(doc, "Arriba a la derecha está el menú con Cerrar sesión.")

    add_heading(doc, "5. Consultar un paquete", 1)
    add_p(doc, "En Paquetes verás el título «Paquetes disponibles · 3». Cada tarjeta muestra el destino, el nombre, la duración y el precio.")
    add_heading(doc, "Pasos", 2)
    add_li(doc, "Pulsa una tarjeta, por ejemplo Cusco esencial.")
    add_li(doc, "Revisa nombre, destino, horario, precio por persona, descripción, qué incluye y el punto de encuentro.")
    add_li(doc, "Pulsa Reservar este paquete para ir al formulario con ese tour ya elegido.")
    add_li(doc, "O pulsa Ver punto de encuentro para abrir el mapa.")
    add_heading(doc, "Catálogo", 2)
    table(doc, ["Paquete", "Destino", "Horario", "Precio", "Encuentro"], [
        ["Cusco esencial", "Cusco", "1 día · 08:00 a 17:00", "S/ 180.00", "Plaza de Armas de Cusco, junto a la fuente central"],
        ["Paracas y Huacachina", "Ica", "1 día · 06:00 a 20:00", "S/ 240.00", "Plaza de Armas de Ica, frente a la municipalidad"],
        ["Lima histórica", "Lima", "Medio día · 09:00 a 13:00", "S/ 95.00", "Plaza San Martín, monumento central"],
    ])
    add_p(doc, "Cusco esencial incluye guía en español, movilidad local y entradas del recorrido; no incluye alimentación ni alojamiento. Paracas y Huacachina incluye transporte desde el encuentro, guía y paseo por la bahía; no incluye almuerzo ni actividades de aventura. Lima histórica incluye guía y entrada a un museo del recorrido; no incluye transporte hasta el encuentro ni comidas.")

    add_heading(doc, "6. Registrar un cliente", 1)
    add_p(doc, "Puedes registrar un viajero desde la sección Clientes o desde el formulario de reserva (botón Registrar cliente).")
    add_heading(doc, "Campos", 2)
    table(doc, ["Campo", "Regla"], [
        ["Nombre completo", "Entre 3 y 80 caracteres."],
        ["DNI", "Exactamente 8 dígitos. No se puede repetir."],
        ["Correo electrónico", "Formato válido, máximo 120 caracteres."],
        ["Teléfono", "Exactamente 9 dígitos."],
    ])
    add_heading(doc, "Pasos", 2)
    add_li(doc, "Pulsa Registrar cliente.")
    add_li(doc, "Completa los cuatro campos.")
    add_li(doc, "Pulsa Guardar cliente. Verás «Cliente guardado.» y volverás a la pantalla anterior.")
    add_p(doc, "Si registras el cliente desde una reserva, al volver queda seleccionado automáticamente.")
    add_heading(doc, "Errores frecuentes", 2)
    table(doc, ["Mensaje", "Qué hacer"], [
        ["Ingresa al menos 3 caracteres.", "Escribe el nombre completo."],
        ["El DNI debe contener 8 dígitos.", "Usa solo números, sin letras ni puntos."],
        ["Ingresa un correo válido.", "Ejemplo: ana@example.com"],
        ["El teléfono debe contener 9 dígitos.", "Usa 9 números, por ejemplo 987654321."],
        ["DNI duplicado / rechazo al guardar", "Ese documento ya existe. Elige el cliente en la lista o usa otro DNI."],
    ])
    add_p(doc, "Ejemplo válido: Ana Estudiante / 12345678 / ana@example.com / 987654321.")

    add_heading(doc, "7. Crear una reserva", 1)
    add_p(doc, "Hay tres caminos: el botón Nueva reserva, Reservar este paquete en el detalle, o Crear reserva desde un cliente. El formulario se titula Reservas.")
    add_heading(doc, "Qué debes elegir", 2)
    add_li(doc, "Cliente (o regístralo en el momento).")
    add_li(doc, "Paquete turístico.")
    add_li(doc, "Fecha de viaje (hoy o futura; el calendario no deja elegir ayer).")
    add_li(doc, "Cantidad de personas, entre 1 y 30.")
    add_p(doc, "El total se calcula solo: precio del paquete × personas. Si falta paquete o la cantidad no es válida, verás: «Selecciona un paquete y una cantidad válida para calcular el total.»")
    add_heading(doc, "Pasos recomendados", 2)
    add_li(doc, "Abre Cusco esencial y pulsa Reservar este paquete.")
    add_li(doc, "Pulsa Registrar cliente, guarda a Ana Estudiante y comprueba que queda seleccionada.")
    add_li(doc, "Pulsa Seleccionar fecha y elige hoy o un día posterior.")
    add_li(doc, "Escribe una cantidad entre 1 y 30, por ejemplo 2. Revisa el total (2 × S/ 180.00 = S/ 360.00).")
    add_li(doc, "Pulsa Guardar reserva.")
    add_p(doc, "La app muestra «Reserva #… guardada en estado PENDIENTE.» y abre el listado de reservas, ya actualizado. No hace falta refrescar a mano. Si pulsas Guardar varias veces seguidas, solo se procesa una escritura a la vez.")
    add_heading(doc, "Si no deja guardar", 2)
    table(doc, ["Mensaje", "Causa"], [
        ["Selecciona un cliente.", "No elegiste viajero en la lista."],
        ["Selecciona un paquete.", "Dejaste la opción por defecto."],
        ["La fecha no puede estar en el pasado. / Selecciona una fecha válida.", "Falta fecha o es anterior a hoy."],
        ["Ingresa entre 1 y 30 personas. / La cantidad debe estar entre 1 y 30 personas.", "Vacío, 0, letras o más de 30."],
    ])

    add_heading(doc, "8. Ver y avanzar una reserva", 1)
    add_p(doc, "En Reservas cada tarjeta muestra el número, el estado, el nombre del paquete, el cliente, la fecha, las personas y el total. Tócala para abrir Detalle de reserva.")
    add_p(doc, "El detalle incluye cliente (nombre, DNI, correo, teléfono), paquete, destino, horario, fecha, personas, precio por persona, total, punto de encuentro, inclusiones y fecha de registro.")
    add_heading(doc, "Estados", 2)
    table(doc, ["Estado", "Cuándo aparece", "Botón disponible"], [
        ["PENDIENTE", "Al guardar la reserva.", "Confirmar reserva"],
        ["CONFIRMADO", "Después de confirmar.", "Finalizar reserva"],
        ["FINALIZADO", "Después de finalizar.", "Ninguno. El estado ya no cambia."],
    ])
    add_heading(doc, "Pasos para confirmar y finalizar", 2)
    add_li(doc, "Abre una reserva PENDIENTE.")
    add_li(doc, "Pulsa Confirmar reserva y acepta. Debe aparecer «Estado actualizado.» y el estado CONFIRMADO.")
    add_li(doc, "Pulsa Finalizar reserva y acepta. El estado queda FINALIZADO y desaparece el botón de avance.")
    add_p(doc, "No se puede saltar de PENDIENTE a FINALIZADO ni volver atrás. El mapa sigue disponible en cualquier estado.")

    add_heading(doc, "9. Punto de encuentro y mapa", 1)
    add_p(doc, "Puedes abrir el mapa desde el detalle del paquete o desde el detalle de la reserva (Ver punto de encuentro). La pantalla se llama Punto de encuentro.")
    add_li(doc, "El marcador del tour no pide permiso de ubicación.")
    add_li(doc, "Centrar punto de encuentro mueve la cámara al marcador.")
    add_li(doc, "Abrir en Google Maps lanza la aplicación de mapas o el navegador.")
    add_li(doc, "Mostrar mi ubicación pide permiso (precisa o aproximada).")
    add_li(doc, "Ajustes de ubicación sirve si el GPS está apagado.")
    add_heading(doc, "Permisos", 2)
    add_p(doc, "Si deniegas el permiso, el encuentro y las reservas siguen funcionando. Si lo deniegas de forma permanente, la app explica cómo habilitarlo en los ajustes de la aplicación. No se pide ubicación en segundo plano.")
    add_heading(doc, "Si el mapa no carga", 2)
    add_p(doc, "Sin API key de Google Maps, Internet inestable o restricciones de la clave, verás un aviso de configuración. Usa Abrir en Google Maps para localizar el encuentro igual. Clientes y reservas no dependen del mapa.")

    add_heading(doc, "10. Cerrar sesión y conservar datos", 1)
    add_li(doc, "En el menú de la esquina, elige Cerrar sesión.")
    add_li(doc, "Confirma. Volverás al login.")
    add_p(doc, "Los clientes y reservas permanecen en el dispositivo. Al volver a ingresar, los verás otra vez. También se conservan si cierras la app o detienes el proceso.")
    add_p(doc, "Se pierden los datos si desinstalas la aplicación o borras los datos de Android (Ajustes > Aplicaciones > Kusi Tours > Almacenamiento > Borrar datos). No hay copia en la nube ni sincronización entre teléfonos.")

    add_heading(doc, "11. Recorrido rápido de demostración", 1)
    add_li(doc, "Prueba Ingresar con campos vacíos y luego con la contraseña incorrecta.")
    add_li(doc, "Entra con demo@kusitours.pe y Kusi2026!.")
    add_li(doc, "Abre Cusco esencial y lee precio, horario, inclusiones y encuentro.")
    add_li(doc, "Reserva el paquete, registra un cliente nuevo y elige fecha y personas.")
    add_li(doc, "Guarda y comprueba el listado en PENDIENTE.")
    add_li(doc, "Abre la reserva, confírmala y luego finalízala.")
    add_li(doc, "Abre Ver punto de encuentro. Prueba denegar y permitir la ubicación.")
    add_li(doc, "Cierra sesión, vuelve a entrar y verifica que todo sigue ahí.")

    add_heading(doc, "12. Preguntas frecuentes", 1)
    add_p(doc, "¿Puedo pagar el tour desde la app?", bold=True)
    add_p(doc, "No. No hay pasarela de pago ni cupos reales de operadores.")
    add_p(doc, "¿Por qué no veo el mapa de Google?", bold=True)
    add_p(doc, "La instalación de demostración puede no llevar API key. Usa Abrir en Google Maps. Quien compile el proyecto debe añadir MAPS_API_KEY en local.properties y reinstalar.")
    add_p(doc, "¿Puedo editar o borrar un cliente o una reserva?", bold=True)
    add_p(doc, "En esta versión el flujo cubre alta, consulta y cambio de estado. No hay pantalla de edición ni eliminación.")
    add_p(doc, "¿Funciona sin Internet?", bold=True)
    add_p(doc, "Sí para login demo, clientes y reservas. El mapa integrado puede no cargar.")
    add_p(doc, "¿El total cambia si más adelante cambia el precio del paquete?", bold=True)
    add_p(doc, "No. Cada reserva guarda la tarifa que tenía al momento de crearla.")
    add_p(doc, "¿Varias personas pueden usar la misma cuenta en distintos teléfonos?", bold=True)
    add_p(doc, "No se sincroniza. Cada instalación tiene su propia base local.")

    add_heading(doc, "13. Aviso", 1)
    add_p(doc, "Kusi Tours es un proyecto académico. Los destinos, horarios y precios no constituyen una oferta comercial. El acceso demo no debe usarse como modelo de autenticación en un producto real.")

    path = OUT / "Manual_Usuario_Kusi_Tours.docx"
    doc.save(path)
    return path


if __name__ == "__main__":
    pptx = build_pptx()
    docx = build_docx()
    print(pptx)
    print(docx)
