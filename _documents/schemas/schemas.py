import cairosvg, os
ICI = os.path.dirname(os.path.abspath(__file__))  # les fichiers sortent a cote du script
ACC="#1F4E79"; GREY="#595959"; LIGHT="#F2F2F2"; CODE="#8B2500"; MONO="Consolas, Menlo, monospace"; SANS="Calibri, Arial, sans-serif"

def cell(x,y,s,fill="white",stroke="#999",text="",color="#222",size=18,mono=False,dash=False):
    extra = ' stroke-dasharray="4,3"' if dash else ""
    svg=f'<rect x="{x}" y="{y}" width="{s}" height="{s}" fill="{fill}" stroke="{stroke}"{extra}/>'
    if text:
        f=MONO if mono else SANS
        svg+=f'<text x="{x+s/2}" y="{y+s/2}" font-family="{f}" font-size="{size}" fill="{color}" text-anchor="middle" dominant-baseline="central">{text}</text>'
    return svg

def mine(x,y,s):
    cx,cy=x+s/2,y+s/2; r=s*0.16
    g=f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="#B03030"/>'
    for dx,dy in [(1,0),(0,1),(1,1),(1,-1)]:
        k=r*1.7/((dx*dx+dy*dy)**0.5)
        g+=f'<line x1="{cx-dx*k}" y1="{cy-dy*k}" x2="{cx+dx*k}" y2="{cy+dy*k}" stroke="#B03030" stroke-width="2"/>'
    return g

def curseur(x,y,s):
    # fleche de souris, pointe sur le centre de la case
    px,py=x+s/2,y+s/2
    pts=[(0,0),(0,17),(4.5,13),(7.5,20),(10.5,18.5),(7.5,11.5),(12.5,11.5)]
    d="M"+" L".join(f"{px+a*1.15} {py+b*1.15}" for a,b in pts)+" Z"
    return f'<path d="{d}" fill="white" stroke="#222" stroke-width="1.6" stroke-linejoin="round"/>'

def coords(ox,oy,s,n):
    g=""
    for i in range(n):
        g+=f'<text x="{ox+i*s+s/2}" y="{oy-9}" font-family="{MONO}" font-size="13" fill="{GREY}" text-anchor="middle">{i}</text>'
        g+=f'<text x="{ox-12}" y="{oy+i*s+s/2}" font-family="{MONO}" font-size="13" fill="{GREY}" text-anchor="middle" dominant-baseline="central">{i}</text>'
    return g

def wrap(w,h,body):
    return f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}"><rect width="{w}" height="{h}" fill="white"/>{body}</svg>'

# 1. Les huit voisines : decalages (dl, dc) autour de (ligne, colonne)
def voisines():
    s=70; ox=40; oy=50; b=""
    b+=f'<text x="{ox}" y="25" font-family="{SANS}" font-size="16" fill="{GREY}">Autour de la case (l, c) : les décalages (dl, dc)</text>'
    for dl in (-1,0,1):
        for dc in (-1,0,1):
            x=ox+(dc+1)*s; y=oy+(dl+1)*s
            if dl==0 and dc==0:
                b+=cell(x,y,s,fill="#DCE6F1",stroke=ACC,text="(l, c)",size=15,mono=True)
            else:
                b+=cell(x,y,s,fill=LIGHT,text=f"({dl:+d},{dc:+d})",size=15,mono=True,color=CODE)
    # legende
    lx=ox+3*s+40
    b+=f'<text x="{lx}" y="{oy+25}" font-family="{SANS}" font-size="16" fill="#222">La voisine de décalage (dl, dc)</text>'
    b+=f'<text x="{lx}" y="{oy+50}" font-family="{SANS}" font-size="16" fill="#222">est la case</text>'
    b+=f'<text x="{lx}" y="{oy+78}" font-family="{MONO}" font-size="16" fill="{CODE}">(ligne + dl, colonne + dc)</text>'
    b+=f'<text x="{lx}" y="{oy+120}" font-family="{SANS}" font-size="16" fill="#222">Le décalage (0, 0) est la case</text>'
    b+=f'<text x="{lx}" y="{oy+145}" font-family="{SANS}" font-size="16" fill="#222">elle-même : on la saute.</text>'
    b+=f'<text x="{lx}" y="{oy+185}" font-family="{SANS}" font-size="16" fill="#222">Deux boucles, de -1 à +1,</text>'
    b+=f'<text x="{lx}" y="{oy+210}" font-family="{SANS}" font-size="16" fill="#222">parcourent les neuf décalages.</text>'
    return wrap(600,290,b)

# 2. Au bord : les decalages qui sortent de la grille
def bord():
    s=56; ox=40; oy=50; b=""
    b+=f'<text x="{ox}" y="25" font-family="{SANS}" font-size="16" fill="{GREY}">Autour de la case (0, 0) d’une grille 3 × 3</text>'
    # grille 3x3 at ox+s (shifted so that the out-of-grid row/col fit)
    for l in range(-1,3):
        for c in range(-1,3):
            x=ox+(c+1)*s; y=oy+(l+1)*s
            inside = 0<=l<3 and 0<=c<3
            if abs(l)<=1 and abs(c)<=1:
                if l==0 and c==0:
                    b+=cell(x,y,s,fill="#DCE6F1",stroke=ACC,text="(0, 0)",size=14,mono=True)
                elif inside:
                    b+=cell(x,y,s,fill=LIGHT,text=f"({l}, {c})",size=14,mono=True,color=CODE)
                else:
                    b+=cell(x,y,s,fill="white",stroke="#C86464",dash=True,text=f"({l}, {c})",size=14,mono=True,color="#C86464")
            elif inside:
                b+=cell(x,y,s,fill="white",stroke="#BBB")
    lx=ox+4*s+40
    for i,t in enumerate(["Cinq des neuf décalages tombent","hors de la grille (en pointillés).","","mines[-1][...] lève","ArrayIndexOutOfBoundsException :","dansLaGrille(l, c) les écarte","avant de lire le tableau."]):
        mono = "mines" in t or "Array" in t or "dansLa" in t
        b+=f'<text x="{lx}" y="{oy+20+i*26}" font-family="{MONO if mono else SANS}" font-size="15" fill="{CODE if mono else "#222"}">{t}</text>'
    return wrap(640,290,b)

# 3. Propagation sur une grille 5x5
def propagation():
    s=52; ox=52; oy=60; n=5
    mines={(0,3),(4,0)}
    def mv(l,c):
        return sum((l+dl,c+dc) in mines for dl in(-1,0,1) for dc in(-1,0,1) if (dl or dc))
    # zone decouverte depuis (2,3) ? compute: click at (2,2)
    import collections
    start=(3,3); seen=set(); q=collections.deque([start])
    while q:
        l,c=q.popleft()
        if (l,c) in seen or not(0<=l<n and 0<=c<n): continue
        seen.add((l,c))
        if mv(l,c)==0:
            for dl in(-1,0,1):
                for dc in(-1,0,1): q.append((l+dl,c+dc))
    b=coords(ox,oy,s,n)
    b+=f'<text x="{ox}" y="25" font-family="{SANS}" font-size="16" fill="{GREY}">Un clic sur la case sous le curseur : deux mines, la zone ouverte en gris clair</text>'
    for l in range(n):
        for c in range(n):
            x=ox+c*s; y=oy+l*s
            if (l,c) in seen:
                k=mv(l,c)
                b+=cell(x,y,s,fill=LIGHT,stroke="#AAA",text=str(k) if k else "",size=18,color="#1F4E79" if k else "#222")
            else:
                b+=cell(x,y,s,fill="#DADADA",stroke="#AAA")
                if (l,c) in mines: b+=mine(x,y,s)
            if (l,c)==start:
                b+=curseur(x,y,s)
    lx=ox+n*s+40
    for i,t in enumerate(["La case cliquée n’a aucune mine","voisine : ses huit voisines","s’ouvrent à leur tour.","","Une voisine sans mine voisine","propage encore ; une voisine","avec un chiffre s’ouvre, puis","s’arrête : le chiffre borde la zone.","","Les cases encore grises n’ont","jamais été visitées : la propagation","ne traverse pas un chiffre."]):
        b+=f'<text x="{lx}" y="{oy+18+i*22}" font-family="{SANS}" font-size="15" fill="#222">{t}</text>'
    return wrap(640,340,b)

# 4. Trace de la recursion sur le test propagation (3x3, mine en (0,0), clic en (2,2))
def trace():
    s=56; ox=52; oy=60; b=""
    mines={(0,0)}
    def mv(l,c): return sum((l+dl,c+dc) in mines for dl in(-1,0,1) for dc in(-1,0,1) if (dl or dc))
    b+=f'<text x="{ox}" y="25" font-family="{SANS}" font-size="16" fill="{GREY}">Le test propagation : mine en (0, 0), clic en (2, 2)</text>'
    for l in range(3):
        for c in range(3):
            x=ox+c*s; y=oy+l*s
            if (l,c)==(0,0): b+=cell(x,y,s,fill="#DADADA")+mine(x,y,s)
            else:
                k=mv(l,c); b+=cell(x,y,s,fill=LIGHT,text=str(k) if k else "",color=ACC)
    b+=curseur(ox+2*s,oy+2*s,s)
    b+=coords(ox,oy,s,3)
    lx=ox+3*s+40
    lines=[("decouvrir(2, 2)",1,"0 mine voisine : propage"),
           ("decouvrir(1, 1)",2,"1 mine voisine : s’ouvre, s’arrête"),
           ("decouvrir(1, 2)",2,"0 : propage à son tour"),
           ("decouvrir(0, 1)",3,"1 : s’ouvre, s’arrête"),
           ("decouvrir(0, 2)",3,"0 : propage..."),
           ("decouvrir(-1, 1)",4,"hors grille : retour"),
           ("decouvrir(0, 1)",4,"déjà DECOUVERTE : retour"),
           ("...",3,"")]
    for i,(call,depth,note) in enumerate(lines):
        y=oy+10+i*24
        b+=f'<text x="{lx+(depth-1)*18}" y="{y}" font-family="{MONO}" font-size="14" fill="{CODE}">{call}</text>'
        b+=f'<text x="{lx+200}" y="{y}" font-family="{SANS}" font-size="14" fill="#222">{note}</text>'
    b+=f'<text x="{lx}" y="{oy+10+9*24}" font-family="{SANS}" font-size="14" fill="{GREY}">Chaque appel attend la fin de ses voisines avant de rendre la main.</text>'
    b+=f'<text x="{lx}" y="{oy+10+10*24}" font-family="{SANS}" font-size="14" fill="{GREY}">La case (0, 0) n’est jamais ouverte : aucun 0 ne la touche.</text>'
    return wrap(660,330,b)

# 5. Cycle des marques
def cycle():
    b=""; y=70; xs=[80,260,440]; names=["CACHEE","DRAPEAU","DOUTE"]
    for x,nm in zip(xs,names):
        b+=f'<rect x="{x-60}" y="{y-22}" width="120" height="44" rx="8" fill="{LIGHT}" stroke="{ACC}"/>'
        b+=f'<text x="{x}" y="{y}" font-family="{MONO}" font-size="15" fill="{CODE}" text-anchor="middle" dominant-baseline="central">{nm}</text>'
    b+=f'<defs><marker id="a" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto"><path d="M0,0 L8,4 L0,8 z" fill="{ACC}"/></marker></defs>'
    for x1,x2 in [(xs[0]+60,xs[1]-60),(xs[1]+60,xs[2]-60)]:
        b+=f'<line x1="{x1}" y1="{y}" x2="{x2}" y2="{y}" stroke="{ACC}" stroke-width="2" marker-end="url(#a)"/>'
    b+=f'<path d="M{xs[2]} {y+22} C {xs[2]} {y+80}, {xs[0]} {y+80}, {xs[0]} {y+24}" fill="none" stroke="{ACC}" stroke-width="2" marker-end="url(#a)"/>'
    b+=f'<text x="{(xs[0]+xs[2])/2}" y="{y+88}" font-family="{SANS}" font-size="14" fill="{GREY}" text-anchor="middle">chaque clic droit passe à l’état suivant</text>'
    b+=f'<rect x="{xs[0]-60}" y="{y+120}" width="120" height="44" rx="8" fill="white" stroke="#AAA"/>'
    b+=f'<text x="{xs[0]}" y="{y+142}" font-family="{MONO}" font-size="15" fill="{GREY}" text-anchor="middle" dominant-baseline="central">DECOUVERTE</text>'
    b+=f'<text x="{xs[0]+80}" y="{y+142}" font-family="{SANS}" font-size="14" fill="#222" dominant-baseline="central">un clic droit n’y change rien, et on n’en sort jamais</text>'
    b+=f'<text x="{xs[1]}" y="{y-40}" font-family="{SANS}" font-size="14" fill="{GREY}" text-anchor="middle">bloque le clic gauche et la propagation</text>'
    b+=f'<text x="{xs[2]+70}" y="{y-8}" font-family="{SANS}" font-size="14" fill="{GREY}">se découvre</text>'
    b+=f'<text x="{xs[2]+70}" y="{y+10}" font-family="{SANS}" font-size="14" fill="{GREY}">comme CACHEE</text>'
    return wrap(640,265,b)

for name,fn in [("voisines",voisines),("bord",bord),("propagation",propagation),("trace",trace),("cycle",cycle)]:
    svg=fn(); open(os.path.join(ICI, f"{name}.svg"),"w").write(svg)
    cairosvg.svg2png(bytestring=svg.encode(), write_to=os.path.join(ICI, f"{name}.png"), output_width=None, scale=2)
print("ok")
