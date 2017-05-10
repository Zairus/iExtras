package zairus.iextras.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import zairus.iextras.tileentity.TileEntityIExecutor;

public class IExecutorPacket extends AbstractPacket
{
	private int x;
	private int y;
	private int z;
	
	private int mode;
	private int action;
	
	public IExecutorPacket()
	{
		;
	}
	
	public IExecutorPacket(int x, int y, int z, int mode, int action)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.mode = mode;
		this.action = action;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(mode);
		buffer.writeInt(action);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
	{
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.mode = buffer.readInt();
		this.action = buffer.readInt();
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
	}
	
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		TileEntity te = player.worldObj.getTileEntity(new BlockPos(this.x, this.y, this.z));
		
		if (te instanceof TileEntityIExecutor)
		{
			TileEntityIExecutor executor = (TileEntityIExecutor)te;
			
			executor.configureWork(this.mode, this.action);
		}
	}
}
