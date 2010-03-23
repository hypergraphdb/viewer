package org.hypergraphdb.viewer.painter;


public class SimpleLabelTooltipNodePainter extends DefaultNodePainter
{

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

	@Override
	public String getTooltip()
	{
		if(nodeView == null)
			return NOT_EDITABLE;
		
		Object o = getHG().get(nodeView.getNode().getHandle());
		return o!= null ? o.toString() : "[null]";
	}
}
