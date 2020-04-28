package org.eclipse.papyrusrt.codegen.lang.cpp.expr;

import org.eclipse.papyrusrt.codegen.lang.cpp.Name;
import org.eclipse.papyrusrt.codegen.lang.cpp.element.CppClass;

public class This extends ElementAccess
{

    public This( CppClass cls )
    {
        super( cls );
    }

    @Override
    public Name getName() { return new Name("this"); }

}
