package org.hypergraphdb.viewer.painter;

/**
 * Simple node painter that displays the full String representation of the
 * node's underlying object as tooltip and a shortened one as node's Label 
 */
public class SimpleLabelTooltipNodePainter extends DefaultNodePainter
{

	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.DefaultNodePainter#getLabel()
	 */
	public String getLabel()
	{
		if(nodeView == null)
			return NOT_EDITABLE;
		
		String val = getTooltip();
		if("".equals(val) || NOT_EDITABLE.equals(val))
		  return "";
		//if(val.length() > 25)
		//    val = getHG().get(getNode().getHandle()).getClass().getSimpleName();
		int index = val.indexOf('@');
		if(index > 0)
		    val = val.substring(0, index);
		return val.substring(val.lastIndexOf('.') + 1);
	}

	/* (non-Javadoc)
	 * @see org.hypergraphdb.viewer.painter.DefaultNodePainter#getTooltip()
	 */
	@Override
	public String getTooltip()
	{
		if(nodeView == null)
			return NOT_EDITABLE;
		
		Object o = getHG().get(nodeView.getNode().getHandle());
		return o!= null ? o.toString() : "[null]";
	}
}
