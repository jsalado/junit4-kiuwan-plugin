// MIT License
//
// Copyright (c) 2018 Marcos Cacabelos Prol
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package mcp.kiuwan.rules.junit;

import com.als.core.AbstractRule;
import com.als.core.RuleContext;
import com.als.core.ast.BaseNode;


/**
 * @author mcprol
 * 
 * This is a empty rule to avoid 'warning' messages in the engine log when the Junit4Kiuwan rules are executed.
 * Remember that all 'violations' are fired by Junit4KiuwanPlugin itself, so we use this empty rule in the ruledefs (rule definitions).
 */
public class Junit4KiuwanEmptyRule extends AbstractRule { 
	public void initialize (RuleContext ctx) { 
		super.initialize(ctx);	
	}

	protected void visit (BaseNode root, final RuleContext ctx) { 
	}
		
	public void postProcess (RuleContext ctx) { 
	}
}

