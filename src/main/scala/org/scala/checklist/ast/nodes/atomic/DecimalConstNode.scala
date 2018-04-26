package org.scala.checklist.ast.nodes.atomic

import org.scala.checklist.ast.visitors.base.ASTVisitor

class DecimalConstNode(val value: Double) extends AtomicNode {
  override def accept[T, G](visitor: ASTVisitor[T, G], context: G): T = visitor.visitDecimalConstNode(value, context)
}
