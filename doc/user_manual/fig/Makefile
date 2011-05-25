PDFFILES=import.pdf

all:${PDFFILES}

%.pdf:%.svg
	inkscape -z --export-pdf=$@ $<

clean:
	rm -rf ${PDFFILES}
