/**
 * Copyright 2017-2020 Philip Helger, pgcc@helger.com
 *
 * Copyright 2011 Google Inc. All Rights Reserved.
 * Author: sreeni@google.com (Sreeni Viswanadha)
 *
 * Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.helger.pgcc.parser;

import java.util.Set;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.string.StringHelper;

/**
 * Describes expansions - entities that may occur on the right hand sides of
 * productions. This is the base class of a bunch of other more specific
 * classes.
 */
public class Expansion
{
  protected static final String EOL = System.getProperty ("line.separator", "\n");

  /**
   * The line and column number of the construct that corresponds most closely
   * to this node.
   */
  private int m_nLine;
  private int m_nColumn;

  /**
   * An internal name for this expansion. This is used to generate parser
   * routines.
   */
  private String m_sInternalName = "";
  private int m_nInternalIndex = -1;

  /**
   * The parser routines are generated in three phases. The generation of the
   * second and third phase are on demand only, and the third phase can be
   * recursive. This variable is used to keep track of the expansions for which
   * phase 3 generations have been already added to a list so that the recursion
   * can be terminated.
   */
  boolean m_phase3done = false;

  /**
   * The parent of this expansion node. In case this is the top level expansion
   * of the production it is a reference to the production node otherwise it is
   * a reference to another Expansion node. In case this is the top level of a
   * lookahead expansion,then the parent is null.
   */
  public Object m_parent;

  /**
   * The ordinal of this node with respect to its parent.
   */
  int m_ordinalBase;

  /**
   * To avoid right-recursive loops when calculating follow sets, we use a
   * generation number which indicates if this expansion was visited by
   * LookaheadWalk.genFollowSet in the same generation. New generations are
   * obtained by incrementing the static counter below, and the current
   * generation is stored in the non-static variable below.
   */
  private static long s_nextGenerationIndex = 1;
  public long m_myGeneration = 0;

  /**
   * This flag is used for bookkeeping by the minimumSize method in class
   * ParseEngine.
   */
  public boolean m_inMinimumSize = false;

  public static void reInit ()
  {
    s_nextGenerationIndex = 1;
  }

  public static long getNextGenerationIndex ()
  {
    return s_nextGenerationIndex++;
  }

  void setInternalName (final String sPrefix, final int nIndex)
  {
    m_sInternalName = sPrefix + nIndex;
    m_nInternalIndex = nIndex;
  }

  void setInternalNameOnly (final String sName)
  {
    m_sInternalName = sName;
  }

  boolean hasNoInternalName ()
  {
    return StringHelper.hasNoText (m_sInternalName);
  }

  String getInternalName ()
  {
    return m_sInternalName;
  }

  int getInternalIndex ()
  {
    return m_nInternalIndex;
  }

  private String _getSimpleName ()
  {
    final String sName = getClass ().getName ();
    // strip the package name
    return sName.substring (sName.lastIndexOf (".") + 1);
  }

  @Nonnull
  protected static StringBuilder dumpPrefix (final int indent)
  {
    final StringBuilder sb = new StringBuilder (indent * 2);
    for (int i = 0; i < indent; i++)
      sb.append ("  ");
    return sb;
  }

  /**
   * @param indent
   *        indentation level
   * @param alreadyDumped
   *        what was already dumped?
   * @return String
   */
  @OverrideOnDemand
  public StringBuilder dump (final int indent, final Set <? super Expansion> alreadyDumped)
  {
    // lol
    assert alreadyDumped.size () >= 0;
    final StringBuilder value = dumpPrefix (indent).append (System.identityHashCode (this))
                                                   .append (" ")
                                                   .append (_getSimpleName ());
    return value;
  }

  /**
   * @param column
   *        the column to set
   */
  final void setColumn (final int column)
  {
    m_nColumn = column;
  }

  /**
   * @return the column
   */
  int getColumn ()
  {
    return m_nColumn;
  }

  /**
   * @param line
   *        the line to set
   */
  final void setLine (final int line)
  {
    m_nLine = line;
  }

  /**
   * @return the line
   */
  int getLine ()
  {
    return m_nLine;
  }

  /**
   * A reimplementing of Object.hashCode() to be deterministic. This uses the
   * line and column fields to generate an arbitrary number - we assume that
   * this method is called only after line and column are set to their actual
   * values.
   */
  @Override
  public int hashCode ()
  {
    return getLine () + getColumn ();
  }

  @Override
  public String toString ()
  {
    return "[" + getLine () + "," + getColumn () + " " + System.identityHashCode (this) + " " + _getSimpleName () + "]";
  }
}
