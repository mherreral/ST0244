#
# define compiler and compiler flag variables
#

JFLAGS = -g -sourcepath src -d ./src/ ./src/*.java
JC = javac


.SUFFIXES: .java .class

#
# the default make target entry
#

default: run
	$(JC) $(JFLAGS) ./src/*.java
	
	
run:
	@echo "Se ha creado un ejecutable en este directorio"
	@echo "Ejecute el programa digitando:"
	@echo "./Unify"
	@echo ""
	@echo "Opcionalmente puede dar una ruta absoluta"
	@echo "como parametro con su propio test"
	@echo '#!/bin/bash' >> ./Unify
	@echo java -cp './src/ Main $$1' >> ./Unify
	@chmod 755 ./Unify
	@echo ""


#
# RM is a predefined macro in make (RM = rm -f)
#

clean:
	$(RM) ./src/*.class
	$(RM) ./Unify
