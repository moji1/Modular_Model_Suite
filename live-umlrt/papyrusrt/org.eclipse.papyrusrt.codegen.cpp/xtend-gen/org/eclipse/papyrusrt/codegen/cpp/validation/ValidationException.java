/**
 * Copyright (c) 2017 Zeligsoft (2009) Ltd and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Ernesto Posse - Initial API and implementation
 */
package org.eclipse.papyrusrt.codegen.cpp.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.xtumlrt.util.DetailedException;
import org.eclipse.papyrusrt.xtumlrt.util.NamesUtil;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * @author epp
 */
@Data
@SuppressWarnings("all")
public class ValidationException extends DetailedException {
  public enum Kind {
    ERROR,
    
    WARNING;
  }
  
  private final ValidationException.Kind kind;
  
  private final EObject element;
  
  private final String shortMsg;
  
  private final String description;
  
  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Validation ");
    String _kindStr = this.kindStr(this.kind);
    _builder.append(_kindStr);
    _builder.append(": ");
    String _trim = this.shortMsg.trim();
    _builder.append(_trim);
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("Element type: ");
    String _elementTypeStr = this.getElementTypeStr();
    _builder.append(_elementTypeStr, "  ");
    _builder.append(" ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("Element qualified name: ");
    String _elementQualifiedNameStr = this.getElementQualifiedNameStr();
    _builder.append(_elementQualifiedNameStr, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("Description: ");
    _builder.append(this.description, "  ");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String kindStr(final ValidationException.Kind kind) {
    String _switchResult = null;
    if (kind != null) {
      switch (kind) {
        case ERROR:
          _switchResult = "error";
          break;
        case WARNING:
          _switchResult = "warning";
          break;
        default:
          _switchResult = "info";
          break;
      }
    } else {
      _switchResult = "info";
    }
    return _switchResult;
  }
  
  public String getElementTypeStr() {
    if ((this.element == null)) {
      return "null element";
    }
    return this.element.eClass().getName();
  }
  
  public String getElementQualifiedNameStr() {
    if ((this.element == null)) {
      return "null element";
    }
    if ((this.element instanceof NamedElement)) {
      final String umlQualifiedName = ((NamedElement)this.element).getQualifiedName();
      if (((umlQualifiedName != null) && (!umlQualifiedName.trim().isEmpty()))) {
        return umlQualifiedName;
      } else {
        return NamesUtil.getEffectiveQualifiedName(this.element);
      }
    }
    if ((this.element instanceof Enum<?>)) {
      return ((Enum<?>)this.element).name();
    }
    if ((this.element instanceof EObject)) {
      return NamesUtil.getEffectiveQualifiedName(this.element);
    }
    String _string = this.element.toString();
    return ("non-UML element: " + _string);
  }
  
  public ValidationException(final ValidationException.Kind kind, final EObject element, final String shortMsg, final String description) {
    super();
    this.kind = kind;
    this.element = element;
    this.shortMsg = shortMsg;
    this.description = description;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.element== null) ? 0 : this.element.hashCode());
    result = prime * result + ((this.shortMsg== null) ? 0 : this.shortMsg.hashCode());
    return prime * result + ((this.description== null) ? 0 : this.description.hashCode());
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ValidationException other = (ValidationException) obj;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.element == null) {
      if (other.element != null)
        return false;
    } else if (!this.element.equals(other.element))
      return false;
    if (this.shortMsg == null) {
      if (other.shortMsg != null)
        return false;
    } else if (!this.shortMsg.equals(other.shortMsg))
      return false;
    if (this.description == null) {
      if (other.description != null)
        return false;
    } else if (!this.description.equals(other.description))
      return false;
    return true;
  }
  
  @Pure
  public ValidationException.Kind getKind() {
    return this.kind;
  }
  
  @Pure
  public EObject getElement() {
    return this.element;
  }
  
  @Pure
  public String getShortMsg() {
    return this.shortMsg;
  }
  
  @Pure
  public String getDescription() {
    return this.description;
  }
}
