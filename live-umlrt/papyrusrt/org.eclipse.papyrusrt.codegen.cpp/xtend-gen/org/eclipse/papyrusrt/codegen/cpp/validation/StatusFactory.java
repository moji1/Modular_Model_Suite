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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.papyrusrt.codegen.CodeGenPlugin;
import org.eclipse.papyrusrt.codegen.cpp.validation.ValidationException;
import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * @author epp
 */
@SuppressWarnings("all")
public class StatusFactory {
  public static Status createErrorStatus(final EObject element, final String msg) {
    return StatusFactory.createErrorStatus(element, msg, "No detailed description available", false);
  }
  
  public static Status createErrorStatus(final EObject element, final String msg, final String description) {
    return StatusFactory.createErrorStatus(element, msg, description, false);
  }
  
  public static Status createErrorStatus(final EObject element, final String msg, final String description, final boolean throwExn) {
    return StatusFactory.createStatus(ValidationException.Kind.ERROR, element, msg, description, throwExn);
  }
  
  public static void addErrorStatus(final EObject element, final String msg, final MultiStatus aggregateStatus) {
    StatusFactory.addStatus(ValidationException.Kind.ERROR, element, msg, "No detailed description available", aggregateStatus);
  }
  
  public static void addErrorStatus(final EObject element, final String msg, final String description, final MultiStatus aggregateStatus) {
    StatusFactory.addStatus(ValidationException.Kind.ERROR, element, msg, description, aggregateStatus);
  }
  
  public static Status createWarningStatus(final EObject element, final String msg) {
    return StatusFactory.createWarningStatus(element, msg, "No detailed description available", false);
  }
  
  public static Status createWarningStatus(final EObject element, final String msg, final String description) {
    return StatusFactory.createWarningStatus(element, msg, description, false);
  }
  
  public static Status createWarningStatus(final EObject element, final String msg, final String description, final boolean throwExn) {
    return StatusFactory.createStatus(ValidationException.Kind.WARNING, element, msg, description, throwExn);
  }
  
  public static void addWarningStatus(final EObject element, final String msg, final MultiStatus aggregateStatus) {
    StatusFactory.addStatus(ValidationException.Kind.WARNING, element, msg, "No detailed description available", aggregateStatus);
  }
  
  public static void addWarningStatus(final EObject element, final String msg, final String description, final MultiStatus aggregateStatus) {
    StatusFactory.addStatus(ValidationException.Kind.WARNING, element, msg, description, aggregateStatus);
  }
  
  public static Status createStatus(final ValidationException.Kind kind, final EObject element, final String msg, final String description, final boolean throwExn) {
    try {
      Status _xblockexpression = null;
      {
        final ValidationException exception = new ValidationException(kind, element, msg, description);
        int _iStatusOfKind = StatusFactory.getIStatusOfKind(kind);
        String _string = exception.toString();
        final Status status = new Status(_iStatusOfKind, CodeGenPlugin.ID, _string, exception);
        if (throwExn) {
          throw exception;
        }
        _xblockexpression = status;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static void addStatus(final ValidationException.Kind kind, final EObject element, final String msg, final String description, final MultiStatus aggregateStatus) {
    final Status status = StatusFactory.createStatus(kind, element, msg, description, false);
    aggregateStatus.add(status);
  }
  
  public static int getIStatusOfKind(final ValidationException.Kind kind) {
    int _switchResult = (int) 0;
    if (kind != null) {
      switch (kind) {
        case ERROR:
          _switchResult = IStatus.ERROR;
          break;
        case WARNING:
          _switchResult = IStatus.WARNING;
          break;
        default:
          _switchResult = IStatus.INFO;
          break;
      }
    } else {
      _switchResult = IStatus.INFO;
    }
    return _switchResult;
  }
}
