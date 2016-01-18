package editor.dataAccess.loader;

import java.io.File;
import java.util.function.Consumer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import descriptors.IToolDescriptor;
import editor.dataAccess.Uris;

public class LogoLoader extends ImageLoader{
	
	private final IToolDescriptor tool;

	public LogoLoader(IToolDescriptor tool, double width, double height, boolean preserveRatio, boolean smoth, Consumer<Image> onFinish){
		super(tool.getOriginRepository().getToolLogo(tool.getName()),  width, height, preserveRatio, smoth, onFinish);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, Consumer<Image> onFinish){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), onFinish);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, double width, double height, boolean preserveRatio, boolean smoth, ImageView imageView){
		super(tool.getOriginRepository().getToolLogo(tool.getName()),  width, height, preserveRatio, smoth, imageView);
		this.tool = tool;
	}
	
	public LogoLoader(IToolDescriptor tool, ImageView imageView){
		super(tool.getOriginRepository().getToolLogo(tool.getName()), imageView);
		this.tool = tool;
	}
	
	@Override
	protected String getCachePath(){
		String repoDir = Uris.CACHE_DIR + Uris.SEP + super.hash(tool.getOriginRepository().getLocation());
		
		File repo = new File(repoDir);
		if(!repo.exists())
			repo.mkdirs();
			
		return repoDir + Uris.SEP + tool.getName();
	}
	

}
