/*******************************************************************************
 * Copyright (c) 2014-2015 Zeligsoft (2009) Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.papyrusrt.codegen.lang.cpp.element;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.papyrusrt.codegen.lang.cpp.CppWriter;
import org.eclipse.papyrusrt.codegen.lang.cpp.HeaderFile;
import org.eclipse.papyrusrt.codegen.lang.cpp.IUserElement;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.Dependency;
import org.eclipse.papyrusrt.codegen.lang.cpp.dep.ElementDependencies;
import org.eclipse.papyrusrt.codegen.lang.cpp.internal.CppFormatter;
import org.eclipse.papyrusrt.codegen.lang.cpp.name.FileName;

public class ElementList extends HeaderFile
{
    private final ElementDependencies extraDeps = new ElementDependencies( this );
    private final List<IUserElement> elements = new ArrayList<IUserElement>();
    private final List<String> declPrefaceTextList = new ArrayList<String>();
    private final List<String> declEndingTextList = new ArrayList<String>();
    private final List<String> defnPrefaceTextList = new ArrayList<String>();
    private final List<String> defnEndingTextList = new ArrayList<String>();
    private boolean interElementSpace = true;
    private boolean writeDecl = true;
    private boolean writeDefn = true;

    public ElementList( FileName name )
    {
        super( name );
    }

    public void addElement( IUserElement... elements )
    {
        for( IUserElement element : elements )
        {
            element.setDefinedIn( this );
            this.elements.add( element );
        }
    }

    /**
     * Insert the given element before the given one.  If the list does not already contain before,
     * then append the new element to the end.
     */
    public void insertElement( IUserElement element, IUserElement before )
    {
        int index = elements.indexOf( before );
        if( index < 0 )
            addElement( element );
        else
            elements.add( index, element );
    }

    /**
     * To add dependencies explicitly defined by the user (with UML Dependency)
     */
    public void addDeclDependency( Dependency dep ) { extraDeps.decl().add( dep ); }
    public void addDefnDependency( Dependency dep ) { extraDeps.defn().add( dep ); }

    public void addDeclPrefaceText( String text ) { addTrimmedText( declPrefaceTextList, text ); }
    public void addDeclEndingText( String text ) { addTrimmedText( declEndingTextList, text ); }
    public void addDefnPrefaceText( String text ) { addTrimmedText( defnPrefaceTextList, text ); }
    public void addDefnEndingText( String text ) { addTrimmedText( defnEndingTextList, text ); }

    private void addTrimmedText( List<String> textList, String text )
    {
        if( text == null ) return;
        char[] array = text.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        int l = text.length();
        if( l == 0 ) return;
        for (int i = 0; i < l; i++)
        {
            // Skip leading spaces
            while( i < l && (array[i] == ' ' || array[i] == '\t') )
                i++;
            while( i < l && array[i] != '\n' )
            {
                stringBuilder.append( array[i] );
                i++;
            }
            if( i < l && array[i] == '\n' )
                stringBuilder.append( array[i] );
        }
        String trimmed = stringBuilder.toString().trim();
        if( ! trimmed.isEmpty() ) textList.add( trimmed );
    }

    public void setInterElementSpace( boolean val ) { interElementSpace = val; }

    public void setWriteDecl( boolean val ) { writeDecl = val; }
    public void setWriteDefn( boolean val ) { writeDefn = val; }

    private boolean writeTextList( CppFormatter fmt, List<String> textList )
    {
        for( String text: textList )
        {
            if( ! fmt.writeLn( text ) ) return false;
            if( interElementSpace && ! fmt.newline() ) return false;
        }
        return true;
    }

    /**
     * Create and return a formatter that will write to the given memory-based output
     * stream.  Mostly for the test suite.
     * @see #write(String) This method will be removed too.
     */
    public boolean write( CppWriter out )
    {
        String protectionMacro = getName().getAbsolutePath().replace( '/', '_' ).toUpperCase() + "_HH";
        if( ! out.decl().newline()
         || ! out.decl().writeLn( "#ifndef " + protectionMacro )
         || ! out.decl().writeLn( "#define " + protectionMacro )
         || ! out.decl().newline()
         || ! out.defn().newline()
         || ! writeInclude( out.defn() )
         || ! out.defn().newline() )
            return false;

        // Enable writes to both streams.  If there are any actual writes then the preceding
        // content will be flushed out to the file before the actual write.  On the other hand,
        // if we get through all of the elements and the files are still in provisional mode,
        // then it does not need to be written.
        if( writeDecl ) out.decl().enableWrites();
        if( writeDefn ) out.defn().enableWrites();

        ElementDependencies deps = new ElementDependencies( this );

        // Collect dependencies for all contained UserElements.  This is done as part of the
        // write operation because we need the full transitive closure of the dependencies.
        // It is possible for this closure to change after an element is added.
        for( IUserElement element : elements )
            element.addDependencies( deps );
        if( ! deps.write( out ) )
            return false;

        // The special list of extra dependencies is written last.  It is not added to the
        // main dependency list because there isn't a way to sort and filter these.
        if( ! extraDeps.write( out ) )
            return false;

        if( ( writeDecl && ! writeTextList( out.decl(), declPrefaceTextList ) )
         || ( writeDefn && ! writeTextList( out.defn(), defnPrefaceTextList ) ) )
            return false;

        // write the elements
        for( IUserElement element : elements )
        {
            if( ! element.write( out ) )
                return false;
            if( interElementSpace && writeDefn && ! out.defn().newline() ) return false;
        }

        if( ( writeDecl && ! writeTextList( out.decl(), declEndingTextList ) )
         || ( writeDefn && ! writeTextList( out.defn(), defnEndingTextList ) ) )
            return false;

        // If the header is still in provisional mode, then nothing else needs to be
        // done.
        if( out.decl().isProvisional() )
            return true;

        // Otherwise the header has real content and the guard macro must be terminated.
        return writeDecl
            && out.decl().newline()
            && out.decl().writeLn( "#endif" )
            && out.decl().newline();
    }
}
