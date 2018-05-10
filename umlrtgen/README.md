# UMLRTGen

### Usage
```
usage: umlrtgen [-h] <path>
 -c,--component <arg>   Component to generate code for.
 -d,--dev               Development mode: this is to be used only when
                        invoking the generator from a development
                        environment.
 -h,--help              Prints this message.
 -l,--loglevel <arg>    Set the level of logging (OFF, SEVERE, INFO,
                        WARNING, CONFIG, FINE, FINER, FINEST). The default
                        is OFF
 -o,--outdir <arg>      Specifies the output folder. By default it is
                        'gen' in the same folder as the input model.
 -p,--plugins <arg>     Specifies the plugins folders of the PapyrusRT
                        installation.
 -q,--quiet             Inhibits printing messages during generation.
 -s,--prtrace           Print the stack trace for exceptions
 -t,--top <arg>         Specify the name of the top capsule. By default it
                        is "Top"
 -x,--toxr              Translate an input UML2 model into an xtUMLrt
                        model instead of generating code.
```

### Example
Generate code for ```Casule1``` and ```Protocol1```

```
java -jar umlrtgen.jar -c Capsule1,Protocol1 /path/to/umlrt_model.uml
```
