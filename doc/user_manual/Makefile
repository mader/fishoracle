INPUTFILES=manual.tex

all:figures manual.pdf

manual.pdf:${INPUTFILES}
	pdflatex manual.tex
	pdflatex manual.tex

figures: 
	cd fig/ && make

spell:manual.tex
	@cat $< | ispell -l -t -d american |\
        sed -e 's/\"\([aouAOU]\)/\1\"/g' |\
        sort -u	
	
clean:
	rm -f *.aux *.log *.out *.pdf *.toc
	cd fig/ && make clean
